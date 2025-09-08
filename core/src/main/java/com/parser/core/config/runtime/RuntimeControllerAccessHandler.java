package com.parser.core.config.runtime;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RuntimeControllerAccessHandler implements AsyncHandlerInterceptor {

    Gson gson;
    RequestMatcher ignoringRequestMatcher;
    Function<Class<?>, ResponseEntity<String>> handlerBeanTypeChecker;

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {

        if (ignoringRequestMatcher.matches(request) || !(handler instanceof HandlerMethod)) {
            return true;
        }

        var handlerMethod = (HandlerMethod) handler;

        var responseEntity = handlerBeanTypeChecker.apply(handlerMethod.getBeanType());

        if (responseEntity == null) {
            return true;
        } else {
            respond(response, responseEntity.getStatusCode(), responseEntity.getBody());
            return false;
        }
    }

    private void respond(HttpServletResponse response, HttpStatusCode status, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status.value());
        response.getWriter().write(gson.toJson(Collections.singletonMap("message", message)));
        response.flushBuffer();
    }
}
