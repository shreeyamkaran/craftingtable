package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.responses.PlanLimitsResponseDTO;
import com.karan.craftingtable.models.responses.TodayUsageResponseDTO;
import com.karan.craftingtable.services.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usage")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<TodayUsageResponseDTO> getTodayUsage() {
        return new ResponseEntity<>(usageService.getTodayUsage(), HttpStatus.OK);
    }

    @GetMapping("/limits")
    public ResponseEntity<PlanLimitsResponseDTO> getCurrentPlanLimits() {
        return new ResponseEntity<>(usageService.getCurrentPlanLimits(), HttpStatus.OK);
    }

}
