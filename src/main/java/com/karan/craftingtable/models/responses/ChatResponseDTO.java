package com.karan.craftingtable.models.responses;

import com.karan.craftingtable.enums.MessageSenderRoleEnum;

import java.time.Instant;
import java.util.List;

public record ChatResponseDTO(
        Long id,
        MessageSenderRoleEnum messageSenderRole,
        List<ChatEventResponseDTO> chatEvents,
        String content,
        Integer tokensUsed,
        Instant createdAt
) { }
