package com.magicdogs.alkywall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlkyWall API")
                        .description("Virtual Wallet Backend - Alkywall is a virtual wallet backend developed in Java, designed to provide basic banking functionalities to its users. With Alkywall, customers can perform transactions, link both physical and virtual cards, store money in a digital environment, and make online payments.")
                        .version("1.0.0")
                );
    }
}

