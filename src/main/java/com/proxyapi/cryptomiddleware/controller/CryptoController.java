package com.proxyapi.cryptomiddleware.controller;

import com.proxyapi.cryptomiddleware.service.CryptoPriceService;
import com.proxyapi.cryptomiddleware.service.CryptoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    private final CryptoPriceService cryptoPriceService;

    public CryptoController(CryptoPriceService cryptoPriceService) {
        this.cryptoPriceService = cryptoPriceService;
    }

    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return cryptoPriceService.getPrices();
    }
}