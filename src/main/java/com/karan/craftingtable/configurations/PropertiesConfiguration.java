package com.karan.craftingtable.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PropertiesConfiguration {

    @Value("${argon2.saltLength}")
    private Integer argon2SaltLength;

    @Value("${argon2.hashLength}")
    private Integer argon2HashLength;

    @Value("${argon2.parallelism}")
    private Integer argon2Parallelism;

    @Value("${argon2.memory}")
    private Integer argon2Memory;

    @Value("${argon2.iteration}")
    private Integer argon2Iteration;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.token.expiration.ms}")
    private Long accessTokenExpirationMs;

    @Value("${jwt.refresh.token.expiration.ms}")
    private Long refreshTokenExpirationMs;

}
