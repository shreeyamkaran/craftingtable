package com.karan.craftingtable.services;

public interface UsageService {

    void recordTokenUsage(Long userId, int actualTokens);

    void checkDailyTokensUsage();

}
