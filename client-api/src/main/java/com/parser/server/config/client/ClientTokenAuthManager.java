package com.parser.server.config.client;

import com.parser.core.client.entity.ClientEntity;
import com.parser.core.client.service.ClientAuthService;
import com.parser.core.exceptions.ForbiddenException;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Component
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class ClientTokenAuthManager implements AuthenticationManager {

    ClientAuthService clientAuthService;

    @Override
    public ClientTokenContextHolder authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        
        ClientEntity client = clientAuthService.findClientByToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        if (!clientAuthService.isClientActive(client)) {
            throw new ForbiddenException();
        }

        // Обновляем время последней активности
        clientAuthService.updateLastActivity(client);

        return new ClientTokenContextHolder(token, client);
    }
}