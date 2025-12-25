package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequestDTO(
        @NotNull Long planId
) { }
