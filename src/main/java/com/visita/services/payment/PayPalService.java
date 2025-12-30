package com.visita.services.payment;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import com.visita.dto.response.PayPalPaymentResponse;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        try {
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedAuth);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            JsonNode response = restTemplate.postForObject(baseUrl + "/v1/oauth2/token", request, JsonNode.class);

            if (response != null && response.has("access_token")) {
                return response.get("access_token").asText();
            }
            throw new RuntimeException("Failed to get PayPal access token");

        } catch (Exception e) {
            log.error("Error getting PayPal token", e);
            throw new WebException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public PayPalPaymentResponse createPayment(BigDecimal amount, String currency, String returnUrl, String cancelUrl) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            // Construct Request Body manually or use DTOs
            Map<String, Object> purchaseUnit = new HashMap<>();
            Map<String, String> amountMap = new HashMap<>();
            amountMap.put("currency_code", currency);
            amountMap.put("value", String.format("%.2f", amount));
            purchaseUnit.put("amount", amountMap);

            Map<String, Object> applicationContext = new HashMap<>();
            applicationContext.put("return_url", returnUrl);
            applicationContext.put("cancel_url", cancelUrl);

            Map<String, Object> body = new HashMap<>();
            body.put("intent", "CAPTURE");
            body.put("purchase_units", Collections.singletonList(purchaseUnit));
            body.put("application_context", applicationContext);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            JsonNode response = restTemplate.postForObject(baseUrl + "/v2/checkout/orders", request, JsonNode.class);

            if (response != null && response.has("id")) {
                String id = response.get("id").asText();
                String status = response.get("status").asText();
                String approveLink = null;

                for (JsonNode link : response.get("links")) {
                    if ("approve".equals(link.get("rel").asText())) {
                        approveLink = link.get("href").asText();
                        break;
                    }
                }

                return PayPalPaymentResponse.builder()
                        .id(id)
                        .status(status)
                        .approveLink(approveLink)
                        .build();
            }
            throw new RuntimeException("Failed to create PayPal payment");

        } catch (Exception e) {
            log.error("Error creating PayPal payment", e);
            throw new WebException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public PayPalPaymentResponse capturePayment(String orderId) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>("", headers);

            String url = baseUrl + "/v2/checkout/orders/" + orderId + "/capture";

            JsonNode response = restTemplate.postForObject(url, request, JsonNode.class);

            if (response != null && response.has("status") && "COMPLETED".equals(response.get("status").asText())) {
                String id = response.get("id").asText();
                String status = response.get("status").asText();
                return PayPalPaymentResponse.builder().id(id).status(status).build();
            }
            throw new RuntimeException(
                    "Capture Status not COMPLETED: " + (response != null ? response.get("status").asText() : "null"));

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Error capturing PayPal payment. Status: {}, Body: {}", e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException("PayPal Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error capturing PayPal payment", e);
            throw new RuntimeException("PayPal Error: " + e.getMessage());
        }
    }
}
