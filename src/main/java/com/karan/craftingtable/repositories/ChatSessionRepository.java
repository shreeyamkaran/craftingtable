package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, ChatSessionEntity.ChatSessionEntityId> {
}
