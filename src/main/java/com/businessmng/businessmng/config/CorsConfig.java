package com.businessmng.businessmng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // TODO: Update allowed origins for production
    // During development, allow localhost ports; in production, use your domain
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:4200",      // Angular dev server
        "http://localhost:3000",       // Alternative port
        "http://127.0.0.1:4200",
        "http://127.0.0.1:3000"
    ));
    
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);  // Allow cookies to be sent
    configuration.setMaxAge(3600L);           // Cache preflight for 1 hour
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
