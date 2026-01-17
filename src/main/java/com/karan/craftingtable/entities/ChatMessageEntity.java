package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.MessageSenderRoleEnum;

public class ChatMessageEntity extends AuditableBaseEntity {

    private Long id;
    private ChatSessionEntity chatSession;
    private MessageSenderRoleEnum messageSenderRole;
    private String content;
    private String toolCalls;
    private Integer tokensUsed;

}
