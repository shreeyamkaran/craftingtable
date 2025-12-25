package com.karan.craftingtable.models.responses;

import java.time.Instant;

public record ProjectResponseDTO(
        Long id,
        String name,
        Instant createdAt,
        Instant lastModifiedAt
) { }
