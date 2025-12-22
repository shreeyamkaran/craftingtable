package com.karan.craftingtable.models.requests;

public record SignInRequestDTO(
        String email,
        String password
) { }
