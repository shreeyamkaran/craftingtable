package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.ChatEventTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_chat_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChatMessageEntity chatMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatEventTypeEnum chatEventType;

    @Column(nullable = false)
    private Integer sequenceOrder;

    @Column(columnDefinition = "text")
    private String content;

    private String filePath; // null unless FILE_EDIT

    @Column(columnDefinition = "text")
    private String metadata;

}
