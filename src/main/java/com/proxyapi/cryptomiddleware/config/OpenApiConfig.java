package com.proxyapi.cryptomiddleware.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Crypto Middleware API",
                version = "1.0.0",
                description = "API for cryptocurrency data with versioning support"
        ),
        servers = {
                @Server(url = "/api/v1", description = "API Version 1")
        }
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cryptoMiddlewareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crypto Middleware API")
                        .description("Proxy API para precios de criptomonedas usando CoinGecko")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación CoinGecko API")
                        .url("https://www.coingecko.com/en/api"));
    }
}
