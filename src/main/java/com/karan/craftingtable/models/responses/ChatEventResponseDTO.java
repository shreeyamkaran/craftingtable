package com.karan.craftingtable.models.responses;

import com.karan.craftingtable.enums.ChatEventTypeEnum;

public record ChatEventResponseDTO(
        Long id,
        ChatEventTypeEnum chatEventType,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) { }
