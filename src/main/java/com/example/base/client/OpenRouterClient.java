package com.example.base.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenRouterClient {
    
    @Value("${openrouter.api.key}")
    private String apiKey;
    
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 20000; // 20秒
    
    @SuppressWarnings("unchecked")  // 型安全性の警告を抑制
    public String generateText(String prompt) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return tryGenerateText(prompt);
            } catch (RuntimeException e) {
                if (e.getMessage().contains("Rate Limit") && retries < MAX_RETRIES - 1) {
                    retries++;
                    System.out.println("Rate limit hit, retrying after " + RETRY_DELAY_MS/1000 + " seconds...");
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("Failed after " + MAX_RETRIES + " retries");
    }

    private String tryGenerateText(String prompt) {
        System.out.println("=== OPENROUTER API REQUEST START ===");
        System.out.println("Prompt: " + prompt);
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "https://katsuki-flashcard.jp");  // あなたのドメイン
        headers.set("X-Title", "Flashcard App");  // OpenRouter要求ヘッダー
        
        System.out.println("API Key: " + apiKey);  // APIキーが正しく設定されているか確認
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "openai/gpt-4o-mini");  // 使用するモデル
        requestBody.put("messages", List.of(message));
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            System.out.println("Sending request to OpenRouter API:");
            System.out.println("URL: " + API_URL);
            System.out.println("Headers: " + headers);
            System.out.println("Request body: " + requestBody);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(API_URL, request, Map.class);
            System.out.println("=== OPENROUTER API RESPONSE ===");
            System.out.println(response);
            
            if (response == null || !response.containsKey("choices")) {
                throw new RuntimeException("Invalid response from OpenRouter API");
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices.isEmpty()) {
                throw new RuntimeException("No choices returned from OpenRouter API");
            }
            
            Map<String, Object> firstChoice = choices.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> messageResponse = (Map<String, Object>) firstChoice.get("message");
            return (String) messageResponse.get("content");
        } catch (Exception e) {
            System.err.println("=== OPENROUTER API ERROR ===");
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate text: " + e.getMessage());
        }
    }
} 