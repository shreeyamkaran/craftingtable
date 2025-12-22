package com.karan.craftingtable.models.requests;

public record SignUpRequestDTO(
        String name,
        String email,
        String password
) { }
