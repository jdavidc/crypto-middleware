package com.proxyapi.cryptomiddleware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    @Bean
    public JtaTransactionManager transactionManager() {
        return new JtaTransactionManager();
    }
}
