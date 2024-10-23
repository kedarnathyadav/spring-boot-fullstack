package com.kedarnath.security;

import com.kedarnath.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/customers",
                                "api/v1/auth/login")
                        .permitAll()  // Allow unauthenticated access to this endpoint
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless session management
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)// Add JWT filter
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}
