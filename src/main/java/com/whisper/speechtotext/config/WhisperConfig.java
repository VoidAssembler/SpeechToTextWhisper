package com.whisper.speechtotext.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "whisper")
public class WhisperConfig {
    private int timeoutSeconds = 300;
}
