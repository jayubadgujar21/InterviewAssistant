package com.interviewassistant.InterviewAssistant.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptResponse {

    private String question;
    private String answer;
}
