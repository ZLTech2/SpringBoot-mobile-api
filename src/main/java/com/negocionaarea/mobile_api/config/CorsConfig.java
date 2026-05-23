package com.negocionaarea.mobile_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Permite qualquer origem (durante desenvolvimento)
        // Em produção, troque por: config.addAllowedOrigin("https://seudominio.com");
        config.addAllowedOriginPattern("*");

        config.addAllowedMethod("*");  // GET, POST, PUT, DELETE, OPTIONS
        config.addAllowedHeader("*");  // Authorization, Content-Type, etc.
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
