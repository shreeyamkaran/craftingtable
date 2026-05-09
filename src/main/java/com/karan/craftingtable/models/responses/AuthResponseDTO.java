package com.karan.craftingtable.models.responses;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        UserProfileResponseDTO user
) { }
