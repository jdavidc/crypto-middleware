package com.proxyapi.cryptomiddleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoMiddlewareApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoMiddlewareApplication.class, args);
    }

}
