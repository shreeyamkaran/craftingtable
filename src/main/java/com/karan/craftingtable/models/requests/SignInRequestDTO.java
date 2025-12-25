package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequestDTO(
        @Email @NotBlank String email,
        @NotBlank String password
) { }
