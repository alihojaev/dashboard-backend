package com.parser.core.common.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class StaticContext implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext context() {
        if (context == null) {
            throw new IllegalStateException("Context not initialized");
        }
        return context;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            context = applicationContext;
        } else {
            throw new IllegalStateException("Context already initialized");
        }
    }
}
