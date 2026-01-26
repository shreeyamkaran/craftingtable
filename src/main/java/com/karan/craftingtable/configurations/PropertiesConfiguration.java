package com.karan.craftingtable.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PropertiesConfiguration {

    @Value("${client.url}")
    private String clientURL;

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

    @Value("${stripe.api.key}")
    private String stripeAPIKey;

    @Value("${stripe.webhook.signing.secret}")
    private String stripeWebhookSigningSecret;

    @Value("${project.free-tier.max-limit}")
    private Integer freeTierProjectsMaxLimit;

    @Value("${spring.ai.openai.api-key}")
    private String springAIOpenAIAPIKey;

    @Value("${spring.ai.openai.base-url}")
    private String springAIOpenAIBaseURL;

    @Value("${spring.ai.openai.chat.options.model}")
    private String springAIOpenAIChatOptionsModel;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private String springAIOpenAIChatOptionsTemperature;

    @Value("${minio.url}")
    private String minioURL;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${minio.project-bucket}")
    private String minioProjectBucket;

}
