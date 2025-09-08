package com.parser.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EntityScan(basePackages = {"com.parser.core", "com.parser.server"})
@EnableJpaRepositories(basePackages = {"com.parser.core", "com.parser.server"})
@SpringBootApplication(scanBasePackages = {"com.parser.core", "com.parser.server"})
public class AdminApi {

    public static void main(String[] args) {
        SpringApplication.run(AdminApi.class, args);
    }
}

