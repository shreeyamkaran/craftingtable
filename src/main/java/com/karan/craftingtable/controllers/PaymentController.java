package com.karan.craftingtable.controllers;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import com.karan.craftingtable.models.responses.PlanResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import com.karan.craftingtable.models.wrappers.APIResponse;
import com.karan.craftingtable.services.PaymentProcessor;
import com.karan.craftingtable.services.PlanService;
import com.karan.craftingtable.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;
    private final PropertiesConfiguration propertiesConfiguration;

    @GetMapping("/plans")
    public ResponseEntity<APIResponse<List<PlanResponseDTO>>> getAllPlans() {
        List<PlanResponseDTO> response = planService.getAllPlans();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @GetMapping("/me/subscriptions")
    public ResponseEntity<APIResponse<SubscriptionResponseDTO>> getMySubscriptions() {
        SubscriptionResponseDTO response = subscriptionService.getMySubscriptions();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @PostMapping("/checkout")
    public ResponseEntity<APIResponse<CheckoutResponseDTO>> createSessionCheckoutURL(
            @RequestBody CheckoutRequestDTO checkoutRequestDTO
    ) {
        CheckoutResponseDTO response = paymentProcessor.createSessionCheckoutURL(checkoutRequestDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @PostMapping("/payment-gateway/portal")
    public ResponseEntity<APIResponse<PaymentGatewayPortalResponseDTO>> openPaymentGatewayPortal() {
        PaymentGatewayPortalResponseDTO response = paymentProcessor.openPaymentGatewayPortal();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @PostMapping("/webhooks")
    public ResponseEntity<?> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSignatureHeader
    ) {
        return paymentProcessor.handleWebhookEvent(payload, stripeSignatureHeader);
    }

}
