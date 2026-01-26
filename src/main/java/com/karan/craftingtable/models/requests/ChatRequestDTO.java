package com.karan.craftingtable.models.requests;

public record ChatRequestDTO(
        Long projectId,
        String userPrompt
) { }
