package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequestDTO(
        @NotBlank @Size(max = 100) String name,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 4, max = 100) String password
) { }
