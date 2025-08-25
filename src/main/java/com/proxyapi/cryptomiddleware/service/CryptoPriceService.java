package com.proxyapi.cryptomiddleware.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CryptoPriceService {

    private final WebClient webClient;

    // Cache: crypto -> { fiat -> price }
    private final Map<String, Map<String, Double>> cache = new ConcurrentHashMap<>();

    public CryptoPriceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.coingecko.com/api/v3")
                .build();
    }

    /**
     * ✅ Método principal: obtiene precios de múltiples cryptos y divisas.
     * Si falla, devuelve cache.
     */
    public Map<String, Object> getPrices(String ids, String vsCurrencies) {
        try {
            Map<String, Map<String, Double>> response =
                    webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/simple/price")
                                    .queryParam("ids", ids)
                                    .queryParam("vs_currencies", vsCurrencies)
                                    .build())
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                            .block();

            if (response != null && !response.isEmpty()) {
                // Actualizamos cache con último request válido
                cache.clear();
                cache.putAll(response);
            }

        } catch (WebClientResponseException.TooManyRequests e) {
            System.out.println("⚠️ API limit reached (429). Returning cached data.");
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching prices: " + e.getMessage());
        }

        // Armamos respuesta enriquecida
        Map<String, Object> enrichedResponse = new LinkedHashMap<>();
        enrichedResponse.put("timestamp", Instant.now().toString());
        enrichedResponse.put("source", "CoinGecko");
        enrichedResponse.put("prices", cache.isEmpty() ? Map.of() : cache);

        return enrichedResponse;
    }

    /**
     * ✅ Auto-refresh de cache cada 2 minutos (solo BTC y ETH en USD por defecto).
     */
    @Scheduled(fixedRate = 120000) // 2 min
    public void refreshPrices() {
        getPrices("bitcoin,ethereum", "usd");
    }
}
