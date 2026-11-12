package com.portfolio.portfolio_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security config: permit all, use AdminTokenFilter for owner-only endpoints.
 * CORS enabled for the frontend origin.
 *
 * @author Kewei
 * @date 2026-11-11
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${admin.token}")
    private String adminToken;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Stateless REST API: no session, so CSRF is not needed
                .csrf(csrf -> csrf.disable())
                // Allow the frontend dev server (localhost:5173) to call this API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // No HTTP session; each request is authenticated on its own
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Allow same-origin iframes so the H2 console page can render
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // Permit all by default; owner-only endpoints are guarded by AdminTokenFilter
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Run admin token check before Spring Security's standard auth filter
                .addFilterBefore(new AdminTokenFilter(adminToken), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * CORS configuration for the frontend dev server.
     * Allowed origin is externalized to cors.allowed-origin in application.yaml.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Only allow the configured frontend origins, not any domain
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow all headers (need X-Admin-Token and other custom headers)
        config.setAllowedHeaders(List.of("*"));
        // Allow cookies for potential credential use by the frontend
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}