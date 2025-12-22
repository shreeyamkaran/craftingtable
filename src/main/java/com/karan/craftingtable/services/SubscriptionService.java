package com.karan.craftingtable.services;

import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;

public interface SubscriptionService {

    SubscriptionResponseDTO getMySubscriptions();

    CheckoutResponseDTO checkout(CheckoutRequestDTO checkoutRequestDTO);

    PaymentGatewayPortalResponseDTO openPaymentGatewayPortal();

}
