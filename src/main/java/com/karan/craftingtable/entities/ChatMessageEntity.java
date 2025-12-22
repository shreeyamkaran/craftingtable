package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.MessageSenderRoleEnum;

import java.time.Instant;

public class ChatMessageEntity extends AuditableBaseEntity {

    private Long id;
    private ChatSessionEntity chatSession;
    private MessageSenderRoleEnum messageSenderRole;
    private String content;
    private String toolCalls;
    private Integer tokensUsed;

}
