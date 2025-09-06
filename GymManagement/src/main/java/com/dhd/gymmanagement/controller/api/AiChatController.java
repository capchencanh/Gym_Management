package com.dhd.gymmanagement.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKeyEnv;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> body) {
        String question = body == null ? null : String.valueOf(body.getOrDefault("question", ""));
        if (question == null) question = "";

        String apiKey = geminiApiKeyEnv;
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("GEMINI_API_KEY");
        }

        String defaultAnswer = "Xin lỗi, hiện tại mình chưa có câu trả lời. Vui lòng thử lại sau.";
        if (apiKey == null || apiKey.isBlank()) {
            return Map.of("answer", defaultAnswer);
        }


        StringBuilder sb = new StringBuilder();
        sb.append("Bạn là trợ lý dinh dưỡng và tập luyện. Hãy đưa ra lời khuyên an toàn, rõ ràng, có thể áp dụng. ");
        Object userProfileObj = body == null ? null : body.get("userProfile");
        if (userProfileObj instanceof Map) {
            Map up = (Map) userProfileObj;
            sb.append("Thông tin người dùng: ");
            if (up.get("name") != null) sb.append("tên=" + up.get("name") + ", ");
            if (up.get("gender") != null) sb.append("giới tính=" + up.get("gender") + ", ");
            if (up.get("birthdate") != null) sb.append("ngày sinh=" + up.get("birthdate") + ", ");
            if (up.get("height") != null) sb.append("chiều cao=" + up.get("height") + "cm, ");
            if (up.get("weight") != null) sb.append("cân nặng=" + up.get("weight") + "kg, ");
            if (up.get("fitness_goal") != null) sb.append("mục tiêu=" + up.get("fitness_goal") + ". ");
        }
        sb.append("Câu hỏi: ").append(question);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", sb.toString());

        Map<String, Object> partsObj = new HashMap<>();
        partsObj.put("parts", List.of(textPart));

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", List.of(partsObj));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> resp = restTemplate.exchange(GEMINI_URL, HttpMethod.POST, entity, Map.class);
            Map<String, Object> bodyResp = resp.getBody();
            if (bodyResp != null) {
                Object candidatesObj = bodyResp.get("candidates");
                if (candidatesObj instanceof List) {
                    List candidates = (List) candidatesObj;
                    if (!candidates.isEmpty()) {
                        Object contentObj = ((Map) candidates.get(0)).get("content");
                        if (contentObj instanceof Map) {
                            Object partsObjResp = ((Map) contentObj).get("parts");
                            if (partsObjResp instanceof List) {
                                List parts = (List) partsObjResp;
                                if (!parts.isEmpty()) {
                                    Object text = ((Map) parts.get(0)).get("text");
                                    if (text != null) {
                                        return Map.of("answer", String.valueOf(text));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }
        return Map.of("answer", defaultAnswer);
    }
}


