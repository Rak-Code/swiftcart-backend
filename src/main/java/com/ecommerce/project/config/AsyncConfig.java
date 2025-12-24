package com.ecommerce.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    // Enables @Async annotation for asynchronous email sending
    // Enables @Scheduled annotation for scheduled tasks (reminder processing)
}
