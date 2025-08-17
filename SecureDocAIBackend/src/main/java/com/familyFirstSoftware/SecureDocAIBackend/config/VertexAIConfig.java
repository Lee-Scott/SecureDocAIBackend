package com.familyFirstSoftware.SecureDocAIBackend.config;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class VertexAIConfig {

    @Bean
    public VertexAI vertexAI(
            @Value("${gemini.project.id}") String projectId,
            @Value("${gemini.location}") String location) throws IOException {
        return new VertexAI(projectId, location);
    }
}
