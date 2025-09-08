package com.parser.server.config;

import com.parser.core.config.runtime.BasicSpringFoxConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientSpringFoxConfig extends BasicSpringFoxConfig {

    public ClientSpringFoxConfig() {
        super("Parser backend", "1.0");
    }
}
