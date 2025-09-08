package com.parser.server.config;

import com.parser.core.auth.role.enums.ModuleUserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ClientModuleContextConfiguration {

    @Bean
    ModuleUserType userType() {
        var userType = ModuleUserType.CLIENT_API;
        log.info("Module {} initializing", userType);
        return userType;
    }
}
