package com.dhd.gymmanagement.service;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class MoMoService {
    
    private static final String MOMO_PARTNER_CODE = "MOMO";
    private static final String MOMO_ACCESS_KEY = "F8BBA842ECF85";
    private static final String MOMO_SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private static final String MOMO_ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String MOMO_RETURN_URL = "http://localhost:8080/api/payment/momo-callback";
    private static final String MOMO_IPN_URL = "http://localhost:8080/api/payment/momo-ipn";
    
    public String createPaymentUrl(String orderId, long amount, String orderInfo) {
        try {
            String requestId = String.valueOf(System.currentTimeMillis());
            
            String rawSignature = "accessKey=" + MOMO_ACCESS_KEY +
                    "&amount=" + amount +
                    "&extraData=" +
                    "&ipnUrl=" + MOMO_IPN_URL +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + MOMO_PARTNER_CODE +
                    "&redirectUrl=" + MOMO_RETURN_URL +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";
            
            String signature = hmacSHA256(MOMO_SECRET_KEY, rawSignature);
            
            String requestBody = "{\n" +
                    "  \"partnerCode\": \"" + MOMO_PARTNER_CODE + "\",\n" +
                    "  \"partnerName\": \"Test\",\n" +
                    "  \"storeId\": \"MomoTestStore\",\n" +
                    "  \"requestId\": \"" + requestId + "\",\n" +
                    "  \"amount\": " + amount + ",\n" +
                    "  \"orderId\": \"" + orderId + "\",\n" +
                    "  \"orderInfo\": \"" + orderInfo + "\",\n" +
                    "  \"redirectUrl\": \"" + MOMO_RETURN_URL + "\",\n" +
                    "  \"ipnUrl\": \"" + MOMO_IPN_URL + "\",\n" +
                    "  \"lang\": \"vi\",\n" +
                    "  \"extraData\": \"\",\n" +
                    "  \"requestType\": \"captureWallet\",\n" +
                    "  \"signature\": \"" + signature + "\"\n" +
                    "}";
            

            
            return MOMO_ENDPOINT;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean verifyPaymentResponse(Map<String, String> queryParams) {
        try {
            String signature = queryParams.get("signature");
            if (signature == null) {
                return false;
            }
            
            String rawSignature = "accessKey=" + queryParams.get("accessKey") +
                    "&amount=" + queryParams.get("amount") +
                    "&extraData=" + queryParams.get("extraData") +
                    "&message=" + queryParams.get("message") +
                    "&orderId=" + queryParams.get("orderId") +
                    "&orderInfo=" + queryParams.get("orderInfo") +
                    "&orderType=" + queryParams.get("orderType") +
                    "&partnerCode=" + queryParams.get("partnerCode") +
                    "&payType=" + queryParams.get("payType") +
                    "&requestId=" + queryParams.get("requestId") +
                    "&responseTime=" + queryParams.get("responseTime") +
                    "&resultCode=" + queryParams.get("resultCode") +
                    "&transId=" + queryParams.get("transId");
            
            String calculatedSignature = hmacSHA256(MOMO_SECRET_KEY, rawSignature);
            return calculatedSignature.equals(signature);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String hmacSHA256(String key, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
