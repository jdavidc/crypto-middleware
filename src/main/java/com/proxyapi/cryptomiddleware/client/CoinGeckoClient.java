package com.proxyapi.cryptomiddleware.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.Map;

@Component
public class CoinGeckoClient {

    private final WebClient webClient;

    public CoinGeckoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.coingecko.com/api/v3").build();
    }

    public Map<String, Double> fetchCryptoPrices() {
        Map<String, Map<String, Double>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/simple/price")
                        .queryParam("ids", "bitcoin,ethereum")
                        .queryParam("vs_currencies", "usd")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                .block();

        Map<String, Double> result = new HashMap<>();
        if (response != null) {
            response.forEach((crypto, priceData) -> {
                result.put(crypto, priceData.get("usd"));
            });
        }
        System.out.println("Llamando a CoinGecko API...");

        return result;
    }
}
