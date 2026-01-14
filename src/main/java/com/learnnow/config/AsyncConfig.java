package com.learnnow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // Minimum threads kept alive
        executor.setMaxPoolSize(5);       // Maximum threads allowed
        executor.setQueueCapacity(500);   // Max tasks waiting in line
        executor.setThreadNamePrefix("EmailThread-");
        executor.initialize();
        return executor;
    }
}