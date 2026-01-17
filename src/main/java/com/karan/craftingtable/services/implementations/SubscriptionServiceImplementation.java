package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.entities.PlanEntity;
import com.karan.craftingtable.entities.SubscriptionEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.enums.SubscriptionStatusEnum;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.mappers.SubscriptionMapper;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import com.karan.craftingtable.repositories.PlanRepository;
import com.karan.craftingtable.repositories.ProjectMemberRepository;
import com.karan.craftingtable.repositories.SubscriptionRepository;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImplementation implements SubscriptionService {

    private final AuthService authService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PropertiesConfiguration propertiesConfiguration;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public SubscriptionResponseDTO getCurrentSubscription() {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        SubscriptionEntity currentSubscription = subscriptionRepository.findByUserAndSubscriptionStatusIn(currentLoggedInUser, Set.of(
                SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.PAST_DUE, SubscriptionStatusEnum.TRIALING
        )).orElse(new SubscriptionEntity());
        return subscriptionMapper.toSubscriptionResponseDTO(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean doesExist = subscriptionRepository.existsByPaymentGatewaySubscriptionId(subscriptionId);
        if (doesExist) return;
        UserEntity user = this.getUser(userId);
        PlanEntity plan = this.getPlan(planId);
        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .plan(plan)
                .subscriptionStatus(SubscriptionStatusEnum.INCOMPLETE)
                .paymentGatewaySubscriptionId(subscriptionId)
                .build();
        subscriptionRepository.save(subscription);
    }

    @Override
    public void updateSubscription(String paymentGatewaySubscriptionId, SubscriptionStatusEnum status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        SubscriptionEntity subscription = this.getSubscription(paymentGatewaySubscriptionId);
        boolean hasSubscriptionUpdated = false;
        if(status != null && status != subscription.getSubscriptionStatus()) {
            subscription.setSubscriptionStatus(status);
            hasSubscriptionUpdated = true;
        }
        if(periodStart != null && !periodStart.equals(subscription.getCurrentSubscriptionStartsAt())) {
            subscription.setCurrentSubscriptionStartsAt(periodStart);
            hasSubscriptionUpdated = true;
        }
        if(periodEnd != null && !periodEnd.equals(subscription.getCurrentSubscriptionEndsAt())) {
            subscription.setCurrentSubscriptionEndsAt(periodEnd);
            hasSubscriptionUpdated = true;
        }
        if(cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated = true;
        }
        if(planId != null && !planId.equals(subscription.getPlan().getId())) {
            PlanEntity newPlan = this.getPlan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionUpdated = true;
        }
        if(hasSubscriptionUpdated) {
            log.debug("Subscription has been updated successfully: {}", paymentGatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void cancelSubscription(String paymentGatewaySubscriptionId) {
        SubscriptionEntity subscription = this.getSubscription(paymentGatewaySubscriptionId);
        subscription.setSubscriptionStatus(SubscriptionStatusEnum.CANCELED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String paymentGatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        SubscriptionEntity subscription = this.getSubscription(paymentGatewaySubscriptionId);
        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentSubscriptionEndsAt();
        subscription.setCurrentSubscriptionStartsAt(newStart);
        subscription.setCurrentSubscriptionEndsAt(periodEnd);
        if (subscription.getSubscriptionStatus() == SubscriptionStatusEnum.INCOMPLETE || subscription.getSubscriptionStatus() == SubscriptionStatusEnum.PAST_DUE) {
            subscription.setSubscriptionStatus(SubscriptionStatusEnum.ACTIVE);
        }
        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String paymentGatewaySubscriptionId) {
        SubscriptionEntity subscription = this.getSubscription(paymentGatewaySubscriptionId);
        if(subscription.getSubscriptionStatus() == SubscriptionStatusEnum.PAST_DUE) {
            log.debug("Subscription is already past due, paymentGatewaySubscriptionId: {}", paymentGatewaySubscriptionId);
            return;
        }
        subscription.setSubscriptionStatus(SubscriptionStatusEnum.PAST_DUE);
        subscriptionRepository.save(subscription);
        // Notify user via email..
    }

    @Override
    public boolean canCreateNewProject() {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        SubscriptionResponseDTO currentSubscription = this.getCurrentSubscription();
        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(currentLoggedInUser.getId());
        if(currentSubscription.plan() == null) {
            return countOfOwnedProjects < propertiesConfiguration.getFreeTierProjectsMaxLimit();
        }
        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
    }

    private UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private PlanEntity getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

    }

    private SubscriptionEntity getSubscription(String paymentGatewaySubscriptionId) {
        return subscriptionRepository.findByPaymentGatewaySubscriptionId(paymentGatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + paymentGatewaySubscriptionId));
    }

}
