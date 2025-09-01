package com.proxyapi.cryptomiddleware.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
                .setPatternParser(new PathPatternParser())
                .addPathPrefix("/api/v1", c -> c.getPackage().getName().startsWith("com.proxyapi.cryptomiddleware.controller.v1"));
    }
}

