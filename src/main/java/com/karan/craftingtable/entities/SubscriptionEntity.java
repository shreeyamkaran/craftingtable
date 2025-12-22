package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.SubscriptionStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

//@Entity
//@Table(name = "t_subscriptions")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserEntity user;
    private PlanEntity plan;
    @Enumerated(value = EnumType.STRING)
    private SubscriptionStatusEnum subscriptionStatus;
    private String paymentGatewayCustomerId;
    private String paymentGatewaySubscriptionId;
    private Instant currentSubscriptionStartsAt;
    private Instant currentSubscriptionEndsAt;
    private Instant cancellationEndsAt;

}
