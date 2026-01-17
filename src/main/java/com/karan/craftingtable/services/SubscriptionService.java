package com.karan.craftingtable.services;

import com.karan.craftingtable.enums.SubscriptionStatusEnum;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;

import java.time.Instant;

public interface SubscriptionService {

    SubscriptionResponseDTO getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String paymentGatewaySubscriptionId, SubscriptionStatusEnum status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String paymentGatewaySubscriptionId);

    void renewSubscriptionPeriod(String paymentGatewaySubscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String paymentGatewaySubscriptionId);

    boolean canCreateNewProject();

}
