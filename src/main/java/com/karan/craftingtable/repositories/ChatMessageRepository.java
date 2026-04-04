package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ChatMessageEntity;
import com.karan.craftingtable.entities.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("""
        SELECT DISTINCT m FROM ChatMessageEntity m\s
        LEFT JOIN FETCH m.chatEvents e\s
        WHERE m.chatSession = :chatSession\s
        ORDER BY m.createdAt ASC, e.sequenceOrder ASC\s
    """)
    List<ChatMessageEntity> findByChatSession(ChatSessionEntity chatSession);

}

//N+1 Query problem
// chat_messages Query 1
// chat_events with chat_message id: 1
// chat_events with chat_message id: 2
// chat_events with chat_message id: 3
// chat_events with chat_message id: 4
//....
// chat_events with chat_message id: N