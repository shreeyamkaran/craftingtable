package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<PlanEntity, Long> {

    Optional<PlanEntity> findByPaymentGatewayPriceId(String id);

}
