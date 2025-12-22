package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.requests.CheckoutRequestDTO;
import com.karan.craftingtable.models.responses.CheckoutResponseDTO;
import com.karan.craftingtable.models.responses.PaymentGatewayPortalResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import com.karan.craftingtable.services.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImplementation implements SubscriptionService {

    @Override
    public SubscriptionResponseDTO getMySubscriptions() {
        return null;
    }

    @Override
    public CheckoutResponseDTO checkout(CheckoutRequestDTO checkoutRequestDTO) {
        return null;
    }

    @Override
    public PaymentGatewayPortalResponseDTO openPaymentGatewayPortal() {
        return null;
    }

}
