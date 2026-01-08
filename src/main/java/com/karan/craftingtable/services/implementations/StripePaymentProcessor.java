package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.entities.PlanEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import com.karan.craftingtable.repositories.PlanRepository;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.PaymentProcessor;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthService authService;
    private final PlanRepository planRepository;
    private final PropertiesConfiguration propertiesConfiguration;

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
        return null;
    }

    @Override
    public ResponseEntity<?> handleWebhookEvent(String payload, String stripeSignatureHeader) {
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
            // TODO: business logic goes here
            log.info("Received Stripe Event for Stripe Object");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }

}
