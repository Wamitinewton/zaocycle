package com.newton.zaocycle.shared.config;

import com.newton.zaocycle.pesticide.domain.service.SafeHarvestCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class DomainServiceConfig {

    @Bean
    public SafeHarvestCalculator safeHarvestCalculator() {
        return new SafeHarvestCalculator(Clock.systemDefaultZone());
    }
}
