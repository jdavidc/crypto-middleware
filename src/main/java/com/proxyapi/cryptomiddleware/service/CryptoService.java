package com.proxyapi.cryptomiddleware.service;

import com.proxyapi.cryptomiddleware.client.CoinGeckoClient;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;

@Service
public class CryptoService {

    private final CoinGeckoClient coinGeckoClient;

    public CryptoService(CoinGeckoClient coinGeckoClient) {
        this.coinGeckoClient = coinGeckoClient;
    }
    @Cacheable("cryptoPrices")
    public Map<String, Double> getPrices() {
        return coinGeckoClient.fetchCryptoPrices();
    }
}
