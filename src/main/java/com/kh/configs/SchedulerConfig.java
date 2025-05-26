package com.kh.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "com.kh.scheduler")
public class SchedulerConfig {
    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);
    // Kích hoạt cơ chế scheduler

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        return scheduler;
    }

    @Scheduled(fixedDelay = 5000)
    public void sayHello() {
        log.info("Hello, World!");
    }
}