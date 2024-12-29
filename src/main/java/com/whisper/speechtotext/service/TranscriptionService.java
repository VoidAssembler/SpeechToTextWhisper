package com.whisper.speechtotext.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whisper.speechtotext.config.WhisperConfig;
import com.whisper.speechtotext.dto.TranscriptionResponse;
import com.whisper.speechtotext.exception.TranscriptionException;
import com.whisper.speechtotext.model.Language;
import com.whisper.speechtotext.model.WhisperModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final WhisperConfig config;

    public TranscriptionResponse transcribe(MultipartFile file, WhisperModel model, Language language) {
        try {
            // Create temporary file
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".tmp";
            Path tempFile = Files.createTempFile("whisper-", extension);
            
            // Copy uploaded file to temporary file
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            try {
                return processWithWhisper(tempFile.toFile(), model, language);
            } finally {
                // Clean up temporary file
                Files.deleteIfExists(tempFile);
            }
        } catch (Exception e) {
            log.error("Error during transcription", e);
            throw new TranscriptionException("Failed to process audio file", e);
        }
    }

    private TranscriptionResponse processWithWhisper(File audioFile, WhisperModel model, Language language) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("whisper");
        command.add(audioFile.getAbsolutePath());
        command.add("--model");
        command.add(model.getValue());
        
        if (language != Language.AUTO) {
            command.add("--language");
            command.add(language.getCode());
        }

        command.add("--output_format");
        command.add("json");
        
        command.add("--output_dir");
        command.add(audioFile.getParent());

        log.info("Executing command: {}", String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug("Whisper output: {}", line);
            }
        }

        boolean completed = process.waitFor(config.getTimeoutSeconds(), TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            throw new TranscriptionException("Transcription process timed out");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new TranscriptionException("Transcription process failed: " + output);
        }

        // Read the JSON output file
        String jsonFileName = audioFile.getName().substring(0, audioFile.getName().lastIndexOf(".")) + ".json";
        Path jsonFile = Path.of(audioFile.getParent(), jsonFileName);
        
        try {
            String jsonContent = Files.readString(jsonFile);
            WhisperOutput whisperOutput = new ObjectMapper().readValue(jsonContent, WhisperOutput.class);
            
            return TranscriptionResponse.success(
                whisperOutput.text,
                language.getCode(),
                whisperOutput.duration,
                model.getValue()
            );
        } finally {
            Files.deleteIfExists(jsonFile);
        }
    }

    private static class WhisperOutput {
        public String text;
        public double duration;
    }
}
