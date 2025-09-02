package com.proxyapi.cryptomiddleware.service;

import com.proxyapi.cryptomiddleware.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PriceUpdateService {
    private final RabbitTemplate rabbitTemplate;

    public PriceUpdateService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    public void processPriceUpdateAsync(Object update) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, update);
    }
}
