package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.entities.PlanEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.enums.SubscriptionStatusEnum;
import com.karan.craftingtable.exceptions.BadRequestException;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import com.karan.craftingtable.repositories.PlanRepository;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.PaymentProcessor;
import com.karan.craftingtable.services.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.Price;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthService authService;
    private final PlanRepository planRepository;
    private final PropertiesConfiguration propertiesConfiguration;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Override
    public CheckoutResponseDTO createSessionCheckoutURL(CheckoutRequestDTO checkoutRequestDTO) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        PlanEntity plan = planRepository.findById(checkoutRequestDTO.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        SessionCreateParams.Builder params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(plan.getPaymentGatewayPriceId())
                                .setQuantity(1L)
                                .build()
                )
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder()
                                .setBillingMode(
                                        SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                        .build()
                                )
                                .build()
                )
                .setSuccessUrl(propertiesConfiguration.getClientURL() + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(propertiesConfiguration.getClientURL() + "/cancel.html")
                .putMetadata("user_id", String.valueOf(currentLoggedInUser.getId()))
                .putMetadata("plan_id", String.valueOf(plan.getId()));
        try {
            String stripeCustomerId = currentLoggedInUser.getPaymentGatewayCustomerId();
            if (stripeCustomerId == null || stripeCustomerId.isBlank()) {
                params.setCustomerEmail(currentLoggedInUser.getEmail());
            } else {
                params.setCustomer(stripeCustomerId);
            }
            Session session = Session.create(params.build());
            return new CheckoutResponseDTO(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PaymentGatewayPortalResponseDTO openPaymentGatewayPortal() {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        String stripeCustomerId = currentLoggedInUser.getPaymentGatewayCustomerId();
        if(stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            throw new BadRequestException("User does not have a Stripe Customer Id, UserId: " + currentLoggedInUser.getId());
        }
        try {
            var portalSession = com.stripe.model.billingportal.Session.create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setReturnUrl(propertiesConfiguration.getClientURL())
                            .build()
            );
            return new PaymentGatewayPortalResponseDTO(portalSession.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> handleWebhookEvents(String payload, String stripeSignatureHeader) {
        String webhookSigningSecret = propertiesConfiguration.getStripeWebhookSigningSecret();
        try {
            Event event = Webhook.constructEvent(payload, stripeSignatureHeader, webhookSigningSecret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if (stripeObject == null) {
                        log.warn("Failed to deserialize webhook object for event: {}", event.getType());
                        return ResponseEntity
                                .status(HttpStatus.OK)
                                .build();
                    }
                } catch (Exception e) {
                    log.error("Unsafe deserialization failed for event: {} :: {}", event.getType(), e.getMessage());
                    throw new RuntimeException("Deserialization failed");
                }
            }
            Map<String,String> metadata = new HashMap<>();
            if (stripeObject instanceof Session session) {
                metadata = session.getMetadata();
            }
            String type = event.getType();
            log.info("Handling stripe event: {}", type);
            switch (type) {
                case "checkout.session.completed" -> handleCheckoutSessionCompletedEvent((Session) stripeObject, metadata);
                case "customer.subscription.updated" -> handleCustomerSubscriptionUpdatedEvent((Subscription) stripeObject);
                case "customer.subscription.deleted" -> handleCustomerSubscriptionDeletedEvent((Subscription) stripeObject);
                case "invoice.paid" -> handleInvoicePaidEvent((Invoice) stripeObject);
                case "invoice.payment_failed" -> handleInvoicePaymentFailedEvent((Invoice) stripeObject);
                default -> log.debug("Ignored event: {}", type);
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCheckoutSessionCompletedEvent(Session session, Map<String, String> metadata) {
        if (session == null) {
            log.error("Session object is null");
            return;
        }
        Long userId = Long.valueOf(metadata.get("user_id"));
        Long planId = Long.valueOf(metadata.get("plan_id"));
        String subscriptionId = session.getSubscription();
        String customerId = session.getCustomer();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getPaymentGatewayCustomerId() == null) {
            user.setPaymentGatewayCustomerId(customerId);
            userRepository.save(user);
        }
        subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId);
    }

    private void handleCustomerSubscriptionUpdatedEvent(Subscription subscription) {
        if (subscription == null) {
            log.error("Subscription object is null inside handleCustomerSubscriptionUpdatedEvent");
            return;
        }
        SubscriptionStatusEnum status = this.mapStripeStatusToEnum(subscription.getStatus());
        if (status == null) {
            log.warn("Unknown status '{}' for subscription {}", subscription.getStatus(), subscription.getId());
            return;
        }
        SubscriptionItem item = subscription.getItems().getData().getFirst();
        Instant periodStart = this.toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = this.toInstant(item.getCurrentPeriodEnd());
        Long planId = this.resolvePlanId(item.getPrice());
        subscriptionService.updateSubscription(
                subscription.getId(), status, periodStart, periodEnd, subscription.getCancelAtPeriodEnd(), planId
        );
    }

    private void handleCustomerSubscriptionDeletedEvent(Subscription subscription) {
        if (subscription == null) {
            log.error("Subscription object is null inside handleCustomerSubscriptionDeletedEvent");
            return;
        }
        subscriptionService.cancelSubscription(subscription.getId());
    }

    private void handleInvoicePaidEvent(Invoice invoice) {
        String subscriptionId = this.extractSubscriptionId(invoice);
        if (subscriptionId == null) {
            log.error("Subscription object is null inside handleInvoicePaidEvent");
            return;
        }
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            var item = subscription.getItems().getData().getFirst();
            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());
            subscriptionService.renewSubscriptionPeriod(subscriptionId, periodStart, periodEnd);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInvoicePaymentFailedEvent(Invoice invoice) {
        String subscriptionId = extractSubscriptionId(invoice);
        if(subscriptionId == null) return;
        subscriptionService.markSubscriptionPastDue(subscriptionId);
    }

    private SubscriptionStatusEnum mapStripeStatusToEnum(String status) {
        return switch (status) {
            case "active" -> SubscriptionStatusEnum.ACTIVE;
            case "trialing" -> SubscriptionStatusEnum.TRIALING;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatusEnum.PAST_DUE;
            case "canceled" -> SubscriptionStatusEnum.CANCELED;
            case "incomplete" -> SubscriptionStatusEnum.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }

    private Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    private Long resolvePlanId(Price price) {
        if (price == null || price.getId() == null) return null;
        return planRepository.findByPaymentGatewayPriceId(price.getId())
                .map(PlanEntity::getId)
                .orElse(null);
    }

    private String extractSubscriptionId(Invoice invoice) {
        var parent = invoice.getParent();
        if (parent == null) return null;
        var subDetails = parent.getSubscriptionDetails();
        if (subDetails == null) return null;
        return subDetails.getSubscription();
    }

}
