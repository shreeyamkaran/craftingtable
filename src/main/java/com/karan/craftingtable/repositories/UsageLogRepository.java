package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.UsageLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UsageLogRepository extends JpaRepository<UsageLogEntity, Long> {

    Optional<UsageLogEntity> findByUserIdAndDate(Long userId, LocalDate today);

}
