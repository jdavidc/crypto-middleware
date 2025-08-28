package com.proxyapi.cryptomiddleware;

import com.proxyapi.cryptomiddleware.service.CryptoPriceService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;
import org.assertj.core.api.SoftAssertions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class CryptoPriceServiceTest {

    // Test data
    private static final String BITCOIN = "bitcoin";
    private static final String ETHEREUM = "ethereum";
    private static final String USD = "usd";
    private static final String EUR = "eur";
    private static final String BTC_PRICE_RESPONSE = "{\"bitcoin\": {\"usd\": 60000, \"eur\": 55000 }}";
    private static final String ETH_PRICE_RESPONSE = "{\"ethereum\": {\"usd\": 3000, \"eur\": 2800 }}";
    private static final String MULTI_PRICE_RESPONSE = 
        "{\"bitcoin\": {\"usd\": 60000, \"eur\": 55000 }, " +
        "\"ethereum\": {\"usd\": 3000, \"eur\": 2800 }}";
    private static final String INVALID_JSON_RESPONSE = "{invalid-json}";
    private static final String EMPTY_RESPONSE = "{}";

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

    @Nested
    @DisplayName("When refreshing prices")
    class RefreshPricesTests {
        
        @Test
        @DisplayName("should update cache with valid response")
        void refreshPrices_shouldUpdateCacheWithValidResponse() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(MULTI_PRICE_RESPONSE)
                    .addHeader("Content-Type", "application/json"));

            // when
            cryptoPriceService.refreshPrices();
            Map<String, Object> response = cryptoPriceService.getPrices("bitcoin,ethereum", "usd,eur");
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(prices)
                    .as("Should contain both cryptocurrencies")
                    .containsKeys(BITCOIN, ETHEREUM);
                
                softly.assertThat(prices.get(BITCOIN))
                    .as("Bitcoin prices should be correct")
                    .containsEntry(USD, 60000.0)
                    .containsEntry(EUR, 55000.0);
                
                softly.assertThat(prices.get(ETHEREUM))
                    .as("Ethereum prices should be correct")
                    .containsEntry(USD, 3000.0)
                    .containsEntry(EUR, 2800.0);
            });
        }
        
        @Test
        @DisplayName("should handle empty response")
        void refreshPrices_shouldHandleEmptyResponse() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(EMPTY_RESPONSE)
                    .addHeader("Content-Type", "application/json"));

            // when
            cryptoPriceService.refreshPrices();
            Map<String, Object> response = cryptoPriceService.getPrices(BITCOIN, USD);
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then
            assertThat(prices).isEmpty();
        }
        
        @Test
        @DisplayName("should handle invalid JSON response")
        void refreshPrices_shouldHandleInvalidJsonResponse() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(INVALID_JSON_RESPONSE)
                    .addHeader("Content-Type", "application/json"));

            // when
            cryptoPriceService.refreshPrices();
            Map<String, Object> response = cryptoPriceService.getPrices(BITCOIN, USD);
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then
            assertThat(prices).isEmpty();
        }
        
        @Test
        @DisplayName("should handle server error")
        void refreshPrices_shouldHandleServerError() {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(500));
            
            // when
            cryptoPriceService.refreshPrices();
            Map<String, Object> response = cryptoPriceService.getPrices(BITCOIN, USD);
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then
            assertThat(prices).isEmpty();
        }

    @Nested
    @DisplayName("When handling rate limiting")
    class RateLimitingTests {
        
        @Test
        @DisplayName("should keep old cache on too many requests")
        void refreshPrices_shouldKeepOldCacheOnTooManyRequests() {
            // given - first valid response
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(MULTI_PRICE_RESPONSE)
                    .addHeader("Content-Type", "application/json"));

            cryptoPriceService.refreshPrices();
            Map<String, Object> responseBefore = cryptoPriceService.getPrices("bitcoin,ethereum", "usd,eur");
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> pricesBefore = (Map<String, Map<String, Double>>) responseBefore.get("prices");

            // when - second response with rate limit
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            cryptoPriceService.refreshPrices();
            
            Map<String, Object> responseAfter = cryptoPriceService.getPrices("bitcoin,ethereum", "usd,eur");
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> pricesAfter = (Map<String, Map<String, Double>>) responseAfter.get("prices");

            // then
            assertThat(pricesAfter).isEqualTo(pricesBefore);
        }
        
        @Test
        @DisplayName("should handle multiple rate limits")
        void refreshPrices_shouldHandleMultipleRateLimits() {
            // given - initial valid response
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(MULTI_PRICE_RESPONSE)
                    .addHeader("Content-Type", "application/json"));

            cryptoPriceService.refreshPrices();
            
            // when - multiple rate limit responses
            int rateLimitCount = 3;
            for (int i = 0; i < rateLimitCount; i++) {
                mockWebServer.enqueue(new MockResponse().setResponseCode(429));
                cryptoPriceService.refreshPrices();
            }
            
            Map<String, Object> response = cryptoPriceService.getPrices("bitcoin,ethereum", "usd,eur");
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then - should still have the original prices
            assertThat(prices)
                .containsKeys(BITCOIN, ETHEREUM)
                .allSatisfy((coin, rates) -> 
                    assertThat(rates).isNotEmpty()
                );
        }

    @Nested
    @DisplayName("When getting prices")
    class GetPricesTests {
        
        @Test
        @DisplayName("should return fallback when cache is empty")
        void getPrices_shouldReturnFallbackWhenCacheEmpty() {
            // when
            Map<String, Object> response = cryptoPriceService.getPrices("bitcoin,ethereum", "usd,eur");
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then
            assertAll(
                () -> assertThat(prices).isNotNull(),
                () -> assertThat(prices).isEmpty()
            );
        }
        
        @Test
        @DisplayName("should return only requested coins and currencies")
        void getPrices_shouldReturnOnlyRequestedCoinsAndCurrencies() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(MULTI_PRICE_RESPONSE)
                    .addHeader("Content-Type", "application/json"));
            
            cryptoPriceService.refreshPrices();

            // when - request only bitcoin and USD
            Map<String, Object> response = cryptoPriceService.getPrices(BITCOIN, USD);
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");

            // then
            assertAll(
                () -> assertThat(prices).containsOnlyKeys(BITCOIN),
                () -> assertThat(prices.get(BITCOIN)).containsOnlyKeys(USD)
            );
        }
        
        @Test
        @DisplayName("should handle empty or null parameters")
        void getPrices_shouldHandleEmptyOrNullParameters() {
            // when
            Map<String, Object> response1 = cryptoPriceService.getPrices("", "");
            Map<String, Object> response2 = cryptoPriceService.getPrices(null, null);
            
            // then
            assertAll(
                () -> assertThat(response1.get("prices")).isInstanceOf(Map.class).as("should handle empty strings"),
                () -> assertThat(response2.get("prices")).isInstanceOf(Map.class).as("should handle null parameters")
            );
        }
    }
    
    @Test
    @DisplayName("should be thread-safe for concurrent access")
    @Timeout(10) // 10 seconds timeout
    void shouldBeThreadSafeForConcurrentAccess() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        ConcurrentHashMap<Integer, Map<String, Map<String, Double>>> results = new ConcurrentHashMap<>();
        
        // Enqueue multiple responses for concurrent access
        for (int i = 0; i < threadCount; i++) {
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(MULTI_PRICE_RESPONSE)
                .addHeader("Content-Type", "application/json"));
        }
        
        // when
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    if (threadId == 0) {
                        // First thread updates the cache
                        cryptoPriceService.refreshPrices();
                    }
                    
                    // All threads try to read
                    Map<String, Object> response = cryptoPriceService.getPrices("bitcoin,ethereum", "usd,eur");
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Double>> prices = (Map<String, Map<String, Double>>) response.get("prices");
                    results.put(threadId, prices);
                    
                } catch (Exception e) {
                    org.junit.jupiter.api.Assertions.fail("Thread " + threadId + " failed: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        // Start all threads at once
        startLatch.countDown();
        
        // Wait for all threads to complete with timeout
        assertTrue(endLatch.await(5, TimeUnit.SECONDS), "Test timed out waiting for threads to complete");
        
        // Shutdown the executor
        executorService.shutdown();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
        
        // then
        assertThat(results).hasSize(threadCount);
        
        // All threads should see either an empty result or the full result, with no partial/inconsistent states
        results.values().forEach(prices -> {
            if (!prices.isEmpty()) {
                assertThat(prices)
                    .containsKeys(BITCOIN, ETHEREUM)
                    .allSatisfy((coin, rates) -> 
                        assertThat(rates).containsKeys(USD, EUR)
                    );
            }
        });
    }
}
    }
}