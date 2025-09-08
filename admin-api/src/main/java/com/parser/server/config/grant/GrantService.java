package com.parser.server.config.grant;

import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;

import java.util.EnumSet;

public interface GrantService {

    boolean matchAny(PermissionType permission, int accessTypeMask);

    boolean matchAll(PermissionType permission, int accessTypeMask);

    boolean hasAny(PermissionType permission, AccessType... accessTypes);

    boolean hasAny(AccessType accessType, EnumSet<PermissionType> permissions);

    boolean hasAny(AccessType accessType, PermissionType... permissions);

    void checkHasAny(PermissionType permission, AccessType... accessTypes);

    void checkHasAny(AccessType accessType, PermissionType... permissions);

    void checkHasAny(AccessType accessType, EnumSet<PermissionType> permissions);
}