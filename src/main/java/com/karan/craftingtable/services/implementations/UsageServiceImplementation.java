package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.services.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImplementation implements UsageService {

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {

    }

    @Override
    public void checkDailyTokensUsage() {

    }
}
