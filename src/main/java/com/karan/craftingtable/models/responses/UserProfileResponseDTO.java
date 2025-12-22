package com.karan.craftingtable.models.responses;

public record UserProfileResponseDTO(
        Long id,
        String email,
        String name,
        String avatarURL
) { }
