package com.karan.craftingtable.services;

import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentProcessor {

    CheckoutResponseDTO createSessionCheckoutURL(CheckoutRequestDTO checkoutRequestDTO);

    PaymentGatewayPortalResponseDTO openPaymentGatewayPortal();

    ResponseEntity<?> handleWebhookEvents(String payload, String stripeSignatureHeader);

}
