package com.karan.craftingtable.models.responses;

public record TodayUsageResponseDTO(
        Integer tokensUsed,
        Integer tokenLimit,
        Integer previewRunning,
        Integer previewsLimit
) { }
