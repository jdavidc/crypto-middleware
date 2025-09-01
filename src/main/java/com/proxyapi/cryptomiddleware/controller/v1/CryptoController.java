package com.proxyapi.cryptomiddleware.controller.v1;

import com.proxyapi.cryptomiddleware.service.CryptoPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/crypto")
public class CryptoController {

    private final CryptoPriceService cryptoPriceService;

    public CryptoController(CryptoPriceService cryptoPriceService) {
        this.cryptoPriceService = cryptoPriceService;
    }

    @GetMapping("/prices")
    public ResponseEntity<Map<String, Object>> getCryptoPrices(
            @RequestParam(defaultValue = "bitcoin") String ids,
            @RequestParam(defaultValue = "usd") String vsCurrencies) {

        Map<String, Object> response = cryptoPriceService.getPrices(ids, vsCurrencies);
        return ResponseEntity.ok(response);
    }
}