package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.responses.PlanResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import com.karan.craftingtable.services.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImplementation implements PlanService {

    @Override
    public List<PlanResponseDTO> getAllPlans() {
        return List.of();
    }

}
