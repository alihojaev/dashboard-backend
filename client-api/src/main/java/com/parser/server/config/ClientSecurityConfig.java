package com.parser.server.config;

import com.google.gson.Gson;
import com.parser.server.config.client.ClientTokenAuthFilter;
import com.parser.server.config.client.ClientTokenAuthManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ClientSecurityConfig {

    public static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/public/**"),
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/static/**"),
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher("/error/**"),
            new AntPathRequestMatcher("/favicon.ico"),
            new AntPathRequestMatcher("/robots.txt"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/webjars/**"),
            new AntPathRequestMatcher("/api/public/**"),
            new AntPathRequestMatcher("/api/verify/password/restore/**"),
            new AntPathRequestMatcher("/api/restore/password/**"),
            new AntPathRequestMatcher("/api/auth/register"),
            new AntPathRequestMatcher("/api/auth/check/**")
    );

    private final ClientTokenAuthManager client;
    Gson gson;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(eh -> eh.authenticationEntryPoint(
                        (request, response, e) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write(gson.toJson(Collections.singletonMap("message", e.getMessage())));
                            response.flushBuffer();
                        }
                ))
                .authorizeHttpRequests(reg -> {
                    reg
                            .requestMatchers(PUBLIC_URLS)
                            .permitAll()
                            .requestMatchers("/**")
                            .authenticated();
                })
                .addFilterAt(new ClientTokenAuthFilter(client, gson), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource cors = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.addExposedHeader("Authorization");

        cors.registerCorsConfiguration(
                "/**",
                config
        );

        return cors;
    }
}
