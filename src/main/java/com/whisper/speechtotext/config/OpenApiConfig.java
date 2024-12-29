package com.whisper.speechtotext.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI whisperOpenAPI() {
        Server devServer = new Server()
            .url("http://localhost:" + serverPort)
            .description("Development server");

        Contact contact = new Contact()
            .name("API Support")
            .url("https://github.com/VoidAssembler/SpeechToTextWhisper")
            .email("your.email@example.com");

        License mitLicense = new License()
            .name("MIT License")
            .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
            .title("Speech to Text API")
            .version("1.0.0")
            .description("REST API для преобразования речи в текст с использованием OpenAI Whisper")
            .contact(contact)
            .license(mitLicense);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer));
    }
}
