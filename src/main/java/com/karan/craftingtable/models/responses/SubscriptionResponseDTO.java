package com.karan.craftingtable.models.responses;

import java.time.Instant;

public record SubscriptionResponseDTO(
        PlanResponseDTO plan,
        String subscriptionStatus,
        Instant currentSubscriptionEndsAt,
        Integer tokenUsedInThisCycle
) { }
