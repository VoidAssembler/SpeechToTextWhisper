package com.whisper.speechtotext.controller;

import com.whisper.speechtotext.dto.TranscriptionResponse;
import com.whisper.speechtotext.model.Language;
import com.whisper.speechtotext.model.WhisperModel;
import com.whisper.speechtotext.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "audio/wav", "audio/mp3", "audio/flac", "audio/aac",
            "audio/mpeg", "audio/x-wav", "audio/x-aac"
    );

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptionResponse> transcribe(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "model", required = false, defaultValue = "base") String model,
            @RequestParam(value = "language", required = false, defaultValue = "auto") String language
    ) {
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(TranscriptionResponse.error("File is empty"));
        }

        // Validate file format
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_FORMATS.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(TranscriptionResponse.error("Unsupported file format"));
        }

        try {
            WhisperModel whisperModel = WhisperModel.fromString(model);
            Language lang = Language.fromString(language);
            
            TranscriptionResponse response = transcriptionService.transcribe(file, whisperModel, lang);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(TranscriptionResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}
