package com.parser.server.config.backend;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.jwt.TokenService;
import com.parser.core.auth.core.service.AuthService;
import com.parser.core.auth.login.service.LoginService;
import com.parser.core.exceptions.ForbiddenException;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Component
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class BackendTokenAuthManager implements AuthenticationManager {

    LoginService loginService;
    TokenService jwt;
    AuthService authService;

    @Override
    public BackendTokenContextHolder authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        AdminEntity manager = loginService.userFromToken(token);

        if (manager == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (authService.findByUsername(manager.getUsername()).isEmpty()) {
            throw new ForbiddenException();
        }

        if (!jwt.verify(token, manager.getUsername(), manager.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return new BackendTokenContextHolder(token, manager);
    }
}