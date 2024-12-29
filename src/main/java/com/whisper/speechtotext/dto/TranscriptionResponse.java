package com.whisper.speechtotext.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranscriptionResponse {
    private String status;
    private TranscriptionData data;

    @Data
    @Builder
    public static class TranscriptionData {
        private String text;
        private String language;
        private double duration;
        private String modelUsed;
    }

    public static TranscriptionResponse success(String text, String language, double duration, String modelUsed) {
        return TranscriptionResponse.builder()
                .status("success")
                .data(TranscriptionData.builder()
                        .text(text)
                        .language(language)
                        .duration(duration)
                        .modelUsed(modelUsed)
                        .build())
                .build();
    }

    public static TranscriptionResponse error(String message) {
        return TranscriptionResponse.builder()
                .status("error")
                .data(TranscriptionData.builder()
                        .text(message)
                        .build())
                .build();
    }
}
