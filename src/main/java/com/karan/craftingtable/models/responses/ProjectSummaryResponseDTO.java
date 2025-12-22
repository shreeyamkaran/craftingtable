package com.karan.craftingtable.models.responses;

import java.time.Instant;

public record ProjectSummaryResponseDTO(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) { }
