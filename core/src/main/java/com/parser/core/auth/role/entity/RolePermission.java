package com.parser.core.auth.role.entity;

import com.parser.core.auth.core.dto.GrantHolder;
import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.config.permission.AccessPermission;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = RolePermission.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermission implements AccessPermission {

    @SqlTable
    public static final String TABLE_NAME = "ROLE_PERMISSION";

    @EmbeddedId
    RolePermissionId id;

    @ManyToOne
    @MapsId("roleId")
    Role role;

    @ManyToOne
    @MapsId("permissionId")
    Permission permission;

    @Column(nullable = false)
    int permissionAccess;

    public RolePermission(Role role, Permission permission, int permissionAccess) {
        this.id = new RolePermissionId();
        this.role = role;
        this.permission = permission;
        this.permissionAccess = permissionAccess;
    }

    public void setPermissionAccess(int permissionAccess) {
        this.permissionAccess = permissionAccess & AccessType.ALL.getMask();
    }

    public GrantHolder toGrantHolder() {
        return new GrantHolder(permission.getName(), getPermissionAccess());
    }

    @Override
    public int getAccess() {
        return getPermissionAccess();
    }
}
