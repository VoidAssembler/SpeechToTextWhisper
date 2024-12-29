package com.whisper.speechtotext.controller;

import com.whisper.speechtotext.dto.TranscriptionResponse;
import com.whisper.speechtotext.model.Language;
import com.whisper.speechtotext.model.WhisperModel;
import com.whisper.speechtotext.service.TranscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Transcription", description = "API для преобразования речи в текст")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "audio/wav", "audio/mp3", "audio/flac", "audio/aac",
            "audio/mpeg", "audio/x-wav", "audio/x-aac",
            "audio/ogg", "application/ogg", "video/ogg"
    );

    @Operation(
        summary = "Преобразовать аудио в текст",
        description = "Загрузите аудиофайл для преобразования в текст. Поддерживаемые форматы: WAV, MP3, FLAC, AAC, OGG"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Успешное преобразование",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TranscriptionResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Неверный формат файла или отсутствует файл",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TranscriptionResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TranscriptionResponse.class)
            )
        )
    })
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptionResponse> transcribe(
            @Parameter(description = "Аудиофайл для преобразования", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Модель Whisper (tiny, base, small, medium, large)", schema = @Schema(allowableValues = {"tiny", "base", "small", "medium", "large"}))
            @RequestParam(value = "model", required = false, defaultValue = "base") String model,
            
            @Parameter(description = "Язык аудио (auto, en, ru, es, fr, de, it, pt, nl, pl, tr, zh)", schema = @Schema(allowableValues = {"auto", "en", "ru", "es", "fr", "de", "it", "pt", "nl", "pl", "tr", "zh"}))
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
                    .body(TranscriptionResponse.error("Unsupported file format. Supported formats: WAV, MP3, FLAC, AAC, OGG"));
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
