package com.parser.server.config;

import com.parser.core.config.runtime.BasicSpringFoxConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSpringFoxConfig extends BasicSpringFoxConfig {

    public AdminSpringFoxConfig() {
        super("Parser backend", "1.0");
    }
}
