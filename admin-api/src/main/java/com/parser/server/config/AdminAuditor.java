package com.parser.server.config;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.server.config.backend.BackendTokenContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdminAuditor implements AuditorAware<UUID> {

    @Override
    public @NotNull Optional<UUID> getCurrentAuditor() {
        return BackendTokenContextHolder.currentOptional()
                .map(BackendTokenContextHolder::getPrincipal)
                .map(AdminEntity::getId);
    }
}
