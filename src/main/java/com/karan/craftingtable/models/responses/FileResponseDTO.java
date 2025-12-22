package com.karan.craftingtable.models.responses;

import java.time.Instant;

public record FileResponseDTO(
        String path,
        Instant modifiedAt,
        Long size,
        String type
) { }
