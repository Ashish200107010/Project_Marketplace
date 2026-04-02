package com.certplatform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Global CORS configuration for the application.
 * Ensures frontend clients can access backend APIs securely.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("[WebConfig] Applying global CORS configuration");

        registry.addMapping("/**")
                // ✅ Externalize allowed origins in application.yml for flexibility
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        log.info("[WebConfig] CORS allowed origins: {}", List.of("http://localhost:5173"));
    }
}
