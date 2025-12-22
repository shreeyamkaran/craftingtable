package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.responses.PlanLimitsResponseDTO;
import com.karan.craftingtable.models.responses.TodayUsageResponseDTO;
import com.karan.craftingtable.services.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImplementation implements UsageService {

    @Override
    public TodayUsageResponseDTO getTodayUsage() {
        return null;
    }

    @Override
    public PlanLimitsResponseDTO getCurrentPlanLimits() {
        return null;
    }

}
