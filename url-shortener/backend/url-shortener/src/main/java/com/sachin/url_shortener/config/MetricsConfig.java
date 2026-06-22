package com.sachin.url_shortener.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("cache.hit")
                .description("Total Redis cache hits")
                .register(registry);
    }

    @Bean
    Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("cache.miss")
                .description("Total Redis cache misses")
                .register(registry);
    }

    @Bean
    Counter cachePutCounter(MeterRegistry registry) {
        return Counter.builder("cache.put")
                .description("Total Redis cache writes")
                .register(registry);
    }
}