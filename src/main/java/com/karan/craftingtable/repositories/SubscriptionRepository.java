package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.SubscriptionEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.enums.SubscriptionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    Optional<SubscriptionEntity> findByUserAndSubscriptionStatusIn(UserEntity currentLoggedInUser, Set<SubscriptionStatusEnum> active);

    Optional<SubscriptionEntity> findByPaymentGatewaySubscriptionId(String paymentGatewaySubscriptionId);

    boolean existsByPaymentGatewaySubscriptionId(String paymentGatewaySubscriptionId);

}
