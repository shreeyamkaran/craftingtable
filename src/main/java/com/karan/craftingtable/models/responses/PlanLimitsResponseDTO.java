package com.karan.craftingtable.models.responses;

public record PlanLimitsResponseDTO(
        String planName,
        Integer maxTokensPerDay,
        Integer maxProjects,
        Boolean isUnlimitedAI
) { }
