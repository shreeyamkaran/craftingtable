package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ChatEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEventEntity, Long> {
}
