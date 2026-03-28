package com.familyFirstSoftware.SecureDocAIBackend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

public class DotenvPropertySourceInitializer implements EnvironmentPostProcessor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(DotenvPropertySourceInitializer.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        logger.info("Starting DotenvPropertySourceInitializer to load .env file.");
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
            });
            if (dotenvMap.isEmpty()) {
                logger.warn(".env file not found or is empty. Skipping property source addition.");
            } else {
                environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
                logger.info("Successfully added 'dotenvProperties' as a property source with highest precedence.");
            }
        } catch (Exception e) {
            logger.error("Error processing .env file", e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
