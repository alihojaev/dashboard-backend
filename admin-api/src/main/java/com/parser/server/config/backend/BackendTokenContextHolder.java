package com.parser.server.config.backend;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.ImmutableAccessPermission;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BackendTokenContextHolder extends AbstractAuthenticationToken {

    AdminEntity principal;
    Object credentials;

    @Getter
    @Setter
    @NonFinal
    Map<PermissionType, ImmutableAccessPermission> methodGrantHolder;

    BackendTokenContextHolder(String token) {
        super(null);
        this.principal = null;
        this.credentials = token;
        setAuthenticated(false);
    }

    BackendTokenContextHolder(String token, AdminEntity principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.credentials = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public AdminEntity getPrincipal() {
        return principal;
    }

    public static Optional<BackendTokenContextHolder> currentOptional() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth == null ? null : ((BackendTokenContextHolder) auth));
    }

    public static BackendTokenContextHolder current() {
        return currentOptional().orElseThrow(IllegalStateException::new);
    }
}
