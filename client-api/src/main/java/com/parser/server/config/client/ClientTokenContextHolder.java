package com.parser.server.config.client;

import com.parser.core.client.entity.ClientEntity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientTokenContextHolder extends AbstractAuthenticationToken {

    ClientEntity principal;
    Object credentials;

    ClientTokenContextHolder(String token) {
        super(null);
        this.principal = null;
        this.credentials = token;
        setAuthenticated(false);
    }

    ClientTokenContextHolder(String token, ClientEntity principal) {
        super(null);
        this.principal = principal;
        this.credentials = token;
        setAuthenticated(true);
    }

    public static Optional<ClientTokenContextHolder> currentOptional() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth instanceof ClientTokenContextHolder)) {
            return Optional.empty();
        }
        return Optional.of((ClientTokenContextHolder) auth);
    }

    public static ClientTokenContextHolder current() {
        return currentOptional().orElseThrow(IllegalStateException::new);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public ClientEntity getPrincipal() {
        return principal;
    }
}
