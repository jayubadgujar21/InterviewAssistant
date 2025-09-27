//package com.interviewassistant.InterviewAssistant.Services;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//
//@Service
//public class DeepgramService {
//
//    @Value("${deepgram.api.key}")
//    private String apiKey;
//
//    private final OkHttpClient client = new OkHttpClient();
//
//    public String transcribe(File file) throws IOException {
//        MediaType mediaType = MediaType.parse("audio/mp3");
//
//        RequestBody body = RequestBody.create(file, mediaType);
//
//        Request request = new Request.Builder()
//                .url("https://api.deepgram.com/v1/listen?punctuate=true&utterances=true")
//                .addHeader("Authorization", "Token " + apiKey)
//                .addHeader("Content-Type", "audio/mp3")
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new RuntimeException("Deepgram API failed: " + response.code() + " - " + response.body().string());
//            }
//
//            String responseBody = response.body().string();
//
//            // ✅ Extract only transcript using Jackson
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(responseBody);
//
//            String transcript = root
//                    .path("results")
//                    .path("channels").get(0)
//                    .path("alternatives").get(0)
//                    .path("transcript").asText();
//
//            System.out.println("📝 Transcribed text: " + transcript);
//
//            return transcript;
//        }
//    }
//}

package com.interviewassistant.InterviewAssistant.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DeepgramService {

    @Value("${deepgram.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String transcribe(File file) throws IOException {
        MediaType mediaType = MediaType.parse("audio/mp3");

        RequestBody body = RequestBody.create(file, mediaType);

        // ✅ Added parameters: punctuate & utterances for better handling of long questions
        String deepgramUrl = "https://api.deepgram.com/v1/listen?punctuate=true&utterances=true";

        Request request = new Request.Builder()
                .url(deepgramUrl)
                .addHeader("Authorization", "Token " + apiKey)
                .addHeader("Content-Type", "audio/mp3")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Deepgram API failed: " + response.code() + " - " + response.body().string());
            }

            String responseBody = response.body().string();

            // ✅ Extract transcript
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            String transcript = root
                    .path("results")
                    .path("channels").get(0)
                    .path("alternatives").get(0)
                    .path("transcript").asText();

            System.out.println("📝 Transcribed text: " + transcript);

            return transcript;
        }
    }
}

