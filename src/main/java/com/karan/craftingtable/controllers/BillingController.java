package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import com.karan.craftingtable.models.responses.PlanResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import com.karan.craftingtable.services.PlanService;
import com.karan.craftingtable.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponseDTO>> getAllPlans() {
        return new ResponseEntity<>(planService.getAllPlans(), HttpStatus.OK);
    }

    @GetMapping("/me/subscriptions")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscriptions() {
        return new ResponseEntity<>(subscriptionService.getMySubscriptions(), HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        return new ResponseEntity<>(subscriptionService.checkout(checkoutRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/payment-gateway/portal")
    public ResponseEntity<PaymentGatewayPortalResponseDTO> openPaymentGatewayPortal() {
        return new ResponseEntity<>(subscriptionService.openPaymentGatewayPortal(), HttpStatus.OK);
    }

}
