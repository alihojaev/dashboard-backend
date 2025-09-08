package com.parser.server.config;

import com.parser.core.client.entity.ClientEntity;
import com.parser.server.config.client.ClientTokenContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

public class ClientAuditor implements AuditorAware<UUID> {

    @Override
    public @NotNull Optional<UUID> getCurrentAuditor() {
        return ClientTokenContextHolder.currentOptional()
                .map(ClientTokenContextHolder::getPrincipal)
                .map(ClientEntity::getId);
    }
}
