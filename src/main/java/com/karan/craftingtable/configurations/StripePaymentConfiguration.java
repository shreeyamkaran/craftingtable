package com.karan.craftingtable.configurations;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StripePaymentConfiguration {

    private final PropertiesConfiguration propertiesConfiguration;

    @PostConstruct
    public void init() {
        Stripe.apiKey = propertiesConfiguration.getStripeAPIKey();
    }

}
