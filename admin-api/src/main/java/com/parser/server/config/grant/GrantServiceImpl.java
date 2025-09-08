package com.parser.server.config.grant;

import com.parser.core.auth.core.dto.GrantHolder;
import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.exceptions.ForbiddenException;
import com.parser.server.config.backend.BackendTokenContextHolder;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Function;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrantServiceImpl implements GrantService {

    private static GrantService instance;

    public static GrantService getInstance() {
        return instance;
    }

    @PostConstruct
    void init() {
        instance = this;
    }

    private boolean filter(Function<GrantHolder, Boolean> filterCondition) {
        var auth = BackendTokenContextHolder.current().getPrincipal();
        return auth.getAuthorities()
                .stream()
                .anyMatch(filterCondition::apply);
    }

    @Override
    public boolean matchAny(PermissionType permission, int accessTypeMask) {
        return filter(grantHolder ->
                grantHolder.getPermission() == permission &&
                        grantHolder.hasAny(accessTypeMask));
    }

    @Override
    public boolean matchAll(PermissionType permission, int accessTypeMask) {
        return filter(grantHolder ->
                grantHolder.getPermission() == permission &&
                        grantHolder.hasAll(accessTypeMask));
    }

    @Override
    public boolean hasAny(PermissionType permission, AccessType... accessTypes) {
        return filter(
                grantHolder ->
                        grantHolder.getPermission() == permission &&
                                grantHolder.hasAll(accessTypes));
    }

    @Override
    public boolean hasAny(AccessType accessType, EnumSet<PermissionType> permissions) {
        return filter(
                grantHolder ->
                        permissions.contains(grantHolder.getPermission()) &&
                                grantHolder.hasAll(accessType)
        );
    }

    @Override
    public boolean hasAny(AccessType accessType, PermissionType... permissions) {
        EnumSet<PermissionType> permissionSet = EnumSet.copyOf(Arrays.asList(permissions));
        return hasAny(accessType, permissionSet);
    }

    @Override
    public void checkHasAny(PermissionType permission, AccessType... accessTypes) {
        if (!hasAny(permission, accessTypes)) {
            throw new ForbiddenException();
        }
    }

    @Override
    public void checkHasAny(AccessType accessType, PermissionType... permissions) {
        if (!hasAny(accessType, permissions)) {
            throw new ForbiddenException();
        }
    }

    @Override
    public void checkHasAny(AccessType accessType, EnumSet<PermissionType> permissions) {
        if (!hasAny(accessType, permissions)) {
            throw new ForbiddenException();
        }
    }
}