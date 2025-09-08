package com.parser.server.config.backend;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class BackendTokenAuthFilter extends BasicAuthenticationFilter {

    private static final String BEARER = "bearer ";

    Gson gson;

    public BackendTokenAuthFilter(AuthenticationManager authenticationManager, Gson gson) {
        super(authenticationManager);
        this.gson = gson;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(AUTHORIZATION);

        if (header == null || !header.toLowerCase().startsWith(BEARER)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = header.split(" ")[1];
            SecurityContextHolder.getContext().setAuthentication(
                    getAuthenticationManager().authenticate(
                            new BackendTokenContextHolder(token)
                    )
            );
        } catch (RuntimeException e) {
            if (!(e instanceof AuthenticationException)) {
                log.error("doFilterInternal(): exc: {}, msg: '{}'", e.getClass().getCanonicalName(), e.getMessage());
            }
            SecurityContextHolder.clearContext();
            writeMessage(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }

    private void writeMessage(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(httpStatus.value());
        response.getWriter().write(gson.toJson(Collections.singletonMap("message", message)));
        response.flushBuffer();
    }
}