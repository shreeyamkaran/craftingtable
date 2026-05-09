package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.UsageLogEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.models.responses.PlanResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import com.karan.craftingtable.repositories.UsageLogRepository;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.SubscriptionService;
import com.karan.craftingtable.services.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImplementation implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthService authService;
    private final SubscriptionService subscriptionService;

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();

        UsageLogEntity todayLog = usageLogRepository.findByUserIdAndDate(userId, today).
                orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        Long userId = currentLoggedInUser.getId();
        SubscriptionResponseDTO subscriptionResponse = subscriptionService.getCurrentSubscription();
        PlanResponseDTO plan = subscriptionResponse.plan();

        LocalDate today = LocalDate.now();

        UsageLogEntity todayLog = usageLogRepository.findByUserIdAndDate(userId, today).
                orElseGet(() -> createNewDailyLog(userId, today));

        if(plan.isUnlimitedAI()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if(currentUsage >=  limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Daily limit reached, Upgrade now");
        }
    }

    private UsageLogEntity createNewDailyLog(Long userId, LocalDate date) {
        UsageLogEntity newLog = UsageLogEntity.builder()
                                                .userId(userId)
                                                .date(date)
                                                .tokensUsed(0)
                                                .build();
        return usageLogRepository.save(newLog);
    }
}
