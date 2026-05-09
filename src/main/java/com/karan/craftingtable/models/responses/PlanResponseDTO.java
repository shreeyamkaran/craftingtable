package com.karan.craftingtable.models.responses;

public record PlanResponseDTO(
        Long id,
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Boolean isUnlimitedAI,
        String price
) { }
