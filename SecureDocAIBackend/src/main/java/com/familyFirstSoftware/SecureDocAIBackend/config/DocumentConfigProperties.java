package com.familyFirstSoftware.SecureDocAIBackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "document")
public class DocumentConfigProperties {
    private Lock lock = new Lock();
    private Ai ai = new Ai();
    private Storage storage = new Storage();
    
    @Data
    public static class Lock {
        private Duration defaultTimeout = Duration.ofMinutes(10);
        private Duration maxTimeout = Duration.ofHours(1);
        private boolean autoCleanupEnabled = true;
        private Duration cleanupInterval = Duration.ofMinutes(5);
    }
    
    @Data
    public static class Ai {
        private String defaultModel = "gpt-4";
        private int maxTokens = 2000;
        private double temperature = 0.7;
    }
    
    @Data
    public static class Storage {
        private String baseDir = System.getProperty("user.home") + "/uploads/";
        private String versionsDir = "versions";
        private long maxFileSize = 10 * 1024 * 1024; // 10MB
        private String[] allowedMimeTypes = {"application/pdf", "text/plain", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
    }
}
