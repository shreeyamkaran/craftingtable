package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<PlanEntity, Long> {

}
