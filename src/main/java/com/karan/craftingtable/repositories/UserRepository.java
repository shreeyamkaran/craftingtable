package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
