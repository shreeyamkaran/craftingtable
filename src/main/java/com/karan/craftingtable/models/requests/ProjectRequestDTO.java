package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequestDTO(
        @NotBlank String name
) { }
