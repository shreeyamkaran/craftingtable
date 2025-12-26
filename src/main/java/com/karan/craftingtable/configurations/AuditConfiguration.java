package com.karan.craftingtable.configurations;

import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AuditConfiguration {

    private final AuthService authService;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                return Optional.ofNullable(authService.getCurrentLoggedInUser())
                        .map(UserEntity::getEmail);
            } catch (Exception e) {
                return Optional.of("system");
            }
        };
    }

}
