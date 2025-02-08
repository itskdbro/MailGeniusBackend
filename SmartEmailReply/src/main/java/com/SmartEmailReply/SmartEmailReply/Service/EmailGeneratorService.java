package com.SmartEmailReply.SmartEmailReply.Service;

import com.SmartEmailReply.SmartEmailReply.Entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {

        // 1. build the prompt
        String prompt = buildPrompt(emailRequest);

        // 2. Craft a request
        Map<String, Object> requestBody = Map.of("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})});

        // 3. Do request and get response
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 4. Extract response and then return the response
        return extractResponse(response);
    }

    // Helper function to extract response
    private String extractResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (Exception e) {
            return "Error Processing request : " + e.getMessage();
        }
    }

    // Helper function to build prompt
    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}
