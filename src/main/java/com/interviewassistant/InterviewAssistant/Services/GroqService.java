package com.interviewassistant.InterviewAssistant.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String ask(String question) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("🧐 Your Question: " + question);

        //Preparing message
        Map<String, Object> message = Map.of("role", "user", "content", question);
        Map<String, Object> body = Map.of(
                "model", "llama3-70b-8192", // Groq popular model
                "messages", List.of(message)
        );

        RequestBody requestBody = RequestBody.create(
                mapper.writeValueAsString(body),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Groq error : " + response);
            }

            String responseBody = response.body().string();
            JsonNode root = mapper.readTree(responseBody);
            String finalAnswer = root
                    .get("choices").get(0).get("message").get("content")
                    .asText();

            System.out.println("✅ Answer: \n\t" + finalAnswer);
            return finalAnswer;
        }
    }
}
