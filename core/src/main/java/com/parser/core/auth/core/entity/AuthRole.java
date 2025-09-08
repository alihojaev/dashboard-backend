package com.parser.core.auth.core.entity;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.util.annotation.gson.GsonIgnore;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = AuthRole.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRole implements Serializable {

    @SqlTable
    public static final String TABLE_NAME = "AUTH_ROLE";

    @EmbeddedId
    AuthRoleId id;

    @ManyToOne
    @GsonIgnore
    @MapsId("authId")
    AdminEntity auth;

    @ManyToOne
    @GsonIgnore
    @MapsId("roleId")
    Role role;

    @Column(name = "ACTIVE")
    Boolean active;

    public static AuthRole createForUpdate(AdminEntity auth, Role role, Boolean active) {
        var ar = new AuthRole();
        ar.id = new AuthRoleId(auth.getId(), role.getId());
        ar.auth = auth;
        ar.role = role;
        ar.active = active;
        return ar;
    }

    public static AuthRole createForInsert(AdminEntity auth, Role role, Boolean active) {
        var ar = new AuthRole();
        ar.id = new AuthRoleId();
        ar.auth = auth;
        ar.role = role;
        ar.active = active;
        return ar;
    }
}