package com.interviewassistant.InterviewAssistant.controller;

import com.interviewassistant.InterviewAssistant.Services.DeepgramService;
import com.interviewassistant.InterviewAssistant.Services.GroqService;
import com.interviewassistant.InterviewAssistant.dto.TranscriptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class InterviewController {

    @Autowired
    private DeepgramService deepgramService;

    @Autowired
    private GroqService groqService;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestParam("file")MultipartFile file){

        try{

            File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
            try(FileOutputStream fileOutputStream = new FileOutputStream(convFile)){
                fileOutputStream.write(file.getBytes());
            }
            // Step 1: Transcribe voice to question
            String question = deepgramService.transcribe(convFile);

            // Step 2: Ask Grooq AI
            String answer = groqService.ask(question);
            TranscriptResponse response=new TranscriptResponse(question,answer);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/ask/text")
    public ResponseEntity<?> askQuestionFromText(@RequestBody Map<String, String> body) {
        try{
            String question = body.get("question");
            String answer = groqService.ask(question);
            return ResponseEntity.ok(new TranscriptResponse(question,answer));

        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }

    }

}
