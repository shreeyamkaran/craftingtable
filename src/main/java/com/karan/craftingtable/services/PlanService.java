package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.PlanResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;

import java.util.List;

public interface PlanService {

    List<PlanResponseDTO> getAllPlans();

}
