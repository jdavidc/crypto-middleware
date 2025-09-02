package com.proxyapi.cryptomiddleware.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String QUEUE_NAME = "price.updates";

    @Bean
    public Queue priceUpdateQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }
}
