package com.visita.services.payment;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.visita.dto.response.MoMoPaymentResponse;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoMoService {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Value("${momo.ipn-url}")
    private String ipnUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPayment(String orderId, String orderInfo, BigDecimal amount) {
        try {
            String requestId = String.valueOf(System.currentTimeMillis());
            String requestType = "payWithMethod";
            String extraData = ""; // pass empty for now
            String amountStr = String.valueOf(amount.longValue());

            // Make orderId unique to avoid duplicate orderId error from MoMo
            String uniqueOrderId = orderId + "_" + requestId;

            // Raw Signature Format:
            // accessKey=$accessKey&amount=$amount&extraData=$extraData&ipnUrl=$ipnUrl&orderId=$orderId&orderInfo=$orderInfo&partnerCode=$partnerCode&redirectUrl=$redirectUrl&requestId=$requestId&requestType=$requestType
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amountStr +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + uniqueOrderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(rawSignature, secretKey);

            Map<String, String> map = new HashMap<>();
            map.put("partnerCode", partnerCode);
            map.put("partnerName", "Visita");
            map.put("storeId", "MomoTestStore");
            map.put("requestId", requestId);
            map.put("amount", amountStr);
            map.put("orderId", uniqueOrderId);
            map.put("orderInfo", orderInfo);
            map.put("redirectUrl", redirectUrl);
            map.put("ipnUrl", ipnUrl);
            map.put("lang", "vi");
            map.put("extraData", extraData);
            map.put("requestType", requestType);
            map.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

            log.info("Sending request to MoMo: {}", map);

            MoMoPaymentResponse response = restTemplate.postForObject(endpoint, request, MoMoPaymentResponse.class);

            if (response != null && response.getPayUrl() != null) {
                return response.getPayUrl();
            } else {
                log.error("MoMo response error: {}", response);
                throw new RuntimeException("Failed to get payment URL from MoMo");
            }

        } catch (Exception e) {
            log.error("Error creating MoMo payment", e);
            throw new WebException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] bytes = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Verifies the signature of a MoMo IPN request.
     * Raw signature format per MoMo docs:
     * accessKey=$accessKey&amount=$amount&extraData=$extraData&message=$message&orderId=$orderId
     * &orderInfo=$orderInfo&orderType=$orderType&partnerCode=$partnerCode&payType=$payType
     * &requestId=$requestId&responseTime=$responseTime&resultCode=$resultCode&transId=$transId
     */
    public boolean verifyIpnSignature(com.visita.dto.request.MoMoIPNRequest request) {
        try {
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + request.getAmount() +
                    "&extraData=" + (request.getExtraData() != null ? request.getExtraData() : "") +
                    "&message=" + (request.getMessage() != null ? request.getMessage() : "") +
                    "&orderId=" + request.getOrderId() +
                    "&orderInfo=" + (request.getOrderInfo() != null ? request.getOrderInfo() : "") +
                    "&orderType=" + (request.getOrderType() != null ? request.getOrderType() : "") +
                    "&partnerCode=" + request.getPartnerCode() +
                    "&payType=" + (request.getPayType() != null ? request.getPayType() : "") +
                    "&requestId=" + request.getRequestId() +
                    "&responseTime=" + request.getResponseTime() +
                    "&resultCode=" + request.getResultCode() +
                    "&transId=" + request.getTransId();

            String computedSignature = hmacSHA256(rawSignature, secretKey);
            boolean isValid = computedSignature.equals(request.getSignature());

            if (!isValid) {
                log.warn("Invalid MoMo IPN signature. Expected: {}, Got: {}", computedSignature,
                        request.getSignature());
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error verifying MoMo IPN signature", e);
            return false;
        }
    }
}
