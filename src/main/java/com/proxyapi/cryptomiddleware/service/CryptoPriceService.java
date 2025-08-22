package com.proxyapi.cryptomiddleware.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CryptoPriceService {

    private final WebClient webClient;
    private final Map<String, Double> cache = new ConcurrentHashMap<>();

    public CryptoPriceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.coingecko.com/api/v3")
                .build();
    }

    public Map<String, Double> getPrices() {
        // 👇 si no hay nada en cache, devolvemos vacío en vez de romper
        return cache.isEmpty() ? Map.of("bitcoin", 0.0, "ethereum", 0.0) : cache;
    }

    // 👇 este método se ejecuta automáticamente cada 2 minutos
    @Scheduled(fixedRate = 120000) // 2 minutos
    public void refreshPrices() {
        try {
            Map<String, Map<String, Double>> response =
                    webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/simple/price")
                                    .queryParam("ids", "bitcoin,ethereum")
                                    .queryParam("vs_currencies", "usd")
                                    .build())
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                            .block();

            if (response != null) {
                Map<String, Double> newPrices = response.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get("usd")));

                cache.clear();
                cache.putAll(newPrices);

                System.out.println("Precios actualizados: " + cache);
            }
        } catch (WebClientResponseException.TooManyRequests e) {
            System.out.println("API limit reached (429). Manteniendo datos en cache.");
        } catch (Exception e) {
            System.out.println("Error al actualizar precios: " + e.getMessage());
        }
    }
}

