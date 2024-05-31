package com.magicdogs.alkywall.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api-docs").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api-docs/swagger-config").permitAll()                     
                        .requestMatchers("/accounts/{userId}").hasRole("ADMIN")
                        .requestMatchers("/fixedTerm/simulate").permitAll()
                        .requestMatchers("/loan/simulate").permitAll()
                        .requestMatchers("/users").hasRole("ADMIN")
                        .requestMatchers("/transactions/**").authenticated()
                        .requestMatchers("/transactions/userId/").hasRole("ADMIN")
                        .requestMatchers("/transactions/id/").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
