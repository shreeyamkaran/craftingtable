package com.karan.craftingtable.models.seeds;

public record PlanSeed(
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Integer maxPreviews,
        Boolean isUnlimitedAI,
        Boolean isActive
) { }
