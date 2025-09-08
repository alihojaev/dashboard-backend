package com.parser.core.config.runtime;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
public abstract class BasicSpringFoxConfig {

    private static final String SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    String infoTitle;
    String infoVersion;


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme(SCHEME)
                                                .bearerFormat(BEARER_FORMAT)
                                )
                )
                .info(
                        new Info()
                                .title(infoTitle)
                                .version(infoVersion)
                                .license(new License())
                );
    }
}
