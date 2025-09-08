package com.parser.server.config;

import com.google.gson.Gson;
import com.parser.server.config.grant.GrantService;
import com.parser.server.config.perms.PermissionAccessHandler;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminWebConfigurer implements WebMvcConfigurer {

    Gson gson;
    GrantService grantService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters
    ) {
        converters.removeIf(c -> c.getSupportedMediaTypes().contains(MediaType.APPLICATION_JSON));

        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setGson(gson);
        gsonHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));

        converters.add(gsonHttpMessageConverter);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermissionAccessHandler(
                grantService,
                gson,
                AdminSecurityConfig.PUBLIC_URLS
        ));
    }
}
