package com.visita.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration for frontend URLs and CORS settings.
 * Centralizes frontend URL configuration to avoid hardcoding.
 */
@Configuration
@ConfigurationProperties(prefix = "app.frontend")
@Getter
@Setter
public class FrontendConfig {

    /**
     * Base URL of the frontend application.
     * Example: "http://localhost:5173" or "https://visita.vercel.app"
     */
    private String url;

    /**
     * Raw allowed origins string (comma-separated for prod env var support).
     */
    private String allowedOrigins;

    /**
     * Returns allowed origins as a List for CORS configuration.
     * Supports comma-separated values from environment variables.
     */
    public List<String> getAllowedOriginsList() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            return List.of();
        }
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
