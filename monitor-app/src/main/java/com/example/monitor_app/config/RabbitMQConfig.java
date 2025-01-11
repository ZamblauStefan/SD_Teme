package com.example.monitor_app.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue energyDataQueue() {
        return new Queue("energy_data_queue", true); // Coada durabilă
    }
}
