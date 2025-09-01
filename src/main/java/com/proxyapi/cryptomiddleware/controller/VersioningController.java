package com.proxyapi.cryptomiddleware.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class VersioningController {

    @GetMapping("/api/versions")
    @Operation(summary = "Get API version information")
    public Map<String, Object> getVersions() {
        return Map.of(
                "currentVersion", "v1",
                "versions", Map.of(
                        "v1", Map.of(
                                "status", "CURRENT",
                                "basePath", "/api/v1",
                                "documentation", "/v3/api-docs"
                        )
                )
        );
    }
}
