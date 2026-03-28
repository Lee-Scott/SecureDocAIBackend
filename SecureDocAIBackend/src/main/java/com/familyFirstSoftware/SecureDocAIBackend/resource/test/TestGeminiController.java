package com.familyFirstSoftware.SecureDocAIBackend.resource.test;

import com.familyFirstSoftware.SecureDocAIBackend.service.test.GeminiTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/test/ai")
@RequiredArgsConstructor
@Slf4j
public class TestGeminiController {

    private final GeminiTestService geminiTestService;

    @GetMapping
    public ResponseEntity<?> testAi() {
        try {
            Map<String, String> response = geminiTestService.callGemini("What is the capital of France?");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error calling Gemini API", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
