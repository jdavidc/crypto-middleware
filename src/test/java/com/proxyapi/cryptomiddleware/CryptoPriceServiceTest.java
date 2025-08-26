package com.proxyapi.cryptomiddleware;


import com.proxyapi.cryptomiddleware.service.CryptoPriceService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoPriceServiceTest {

    private static MockWebServer mockWebServer;
    private CryptoPriceService cryptoPriceService;

    @BeforeAll
    static void setupMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdownMockServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        WebClient.Builder builder = WebClient.builder();
        String mockBaseUrl = mockWebServer.url("/").toString();
        cryptoPriceService = new CryptoPriceService(builder, mockBaseUrl);
    }

    @Test
    void refreshPrices_shouldUpdateCacheWithValidResponse() {
        // given
        String jsonResponse = "{ \"bitcoin\": { \"usd\": 60000 }, \"ethereum\": { \"usd\": 3000 } }";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        // when
        cryptoPriceService.refreshPrices();
        Map<String, Object> response = cryptoPriceService.getPrices("bitcoin,ethereum", "usd");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

        // then
        assertThat(prices.get("bitcoin").get("usd")).isEqualTo(60000.0);
        assertThat(prices.get("ethereum").get("usd")).isEqualTo(3000.0);
    }

    @Test
    void refreshPrices_shouldKeepOldCacheOnTooManyRequests() {
        // given
        // primera respuesta válida
        String jsonResponse = "{ \"bitcoin\": { \"usd\": 60000 }, \"ethereum\": { \"usd\": 3000 } }";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        cryptoPriceService.refreshPrices();
        Map<String, Object> responseBefore = cryptoPriceService.getPrices("bitcoin,ethereum", "usd");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> pricesBefore = (Map<String, Map<String, Double>>) responseBefore.get("prices");

        // segunda respuesta simulando 429
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429));

        // when
        cryptoPriceService.refreshPrices();
        Map<String, Object> responseAfter = cryptoPriceService.getPrices("bitcoin,ethereum", "usd");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> pricesAfter = (Map<String, Map<String, Double>>) responseAfter.get("prices");

        // then
        assertThat(pricesAfter).isEqualTo(pricesBefore);
    }

    @Test
    void getPrices_shouldReturnFallbackWhenCacheEmpty() {
        // when
        Map<String, Object> response = cryptoPriceService.getPrices("bitcoin,ethereum", "usd");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

        // then
        assertThat(prices).isNotNull();
        assertThat(prices).isEmpty();
    }
}

