package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.PlanLimitsResponseDTO;
import com.karan.craftingtable.models.responses.TodayUsageResponseDTO;

public interface UsageService {

    TodayUsageResponseDTO getTodayUsage();

    PlanLimitsResponseDTO getCurrentPlanLimits();

}
