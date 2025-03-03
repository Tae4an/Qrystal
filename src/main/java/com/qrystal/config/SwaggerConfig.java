package com.qrystal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI qrystalOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Qrystal 모의고사 플랫폼 API")
                        .description("Qrystal 모의고사 플랫폼의 API 문서")
                        .version("v1.0.0"));
    }
}
