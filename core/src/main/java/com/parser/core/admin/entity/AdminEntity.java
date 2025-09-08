package com.parser.core.admin.entity;

import com.parser.core.auth.core.dto.GrantHolder;
import com.parser.core.auth.core.entity.AuthRole;
import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.common.entity.base.BaseEntity;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = AdminEntity.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEntity extends BaseEntity implements UserDetails, IdBased {

    @SqlTable
    public static final String TABLE_NAME = "admins";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    UUID id;

    String firstName;
    String lastName;
    String phone;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String password;

    @OneToMany(mappedBy = "auth", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<AuthRole> authRoles;

    LocalDateTime lastActivity;

    @Transient
    transient Collection<GrantHolder> authorities;

    Boolean blocked;

    public AdminEntity(UUID id) {
        this.id = id;
    }

    public AdminEntity(UUID id,
                       String password,
                       String username,
                       LocalDateTime lastActivity,
                       UUID createdBy,
                       UUID modifiedBy,
                       LocalDateTime cdt,
                       LocalDateTime mdt,
                       LocalDateTime rdt,
                       Set<AuthRole> roles,
                       Boolean blocked,
                       String firstName,
                       String lastName,
                       String phone) {
        super(createdBy, modifiedBy, cdt, mdt, rdt);
        this.id = id;
        this.password = password;
        this.username = username;
        this.lastActivity = lastActivity;
        setRoles(roles);
        this.blocked = blocked;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public void setRoles(Set<AuthRole> authRoles) {
        this.authRoles = authRoles;

        this.authorities = authRoles == null ? Collections.emptySet() : authRoles.stream().filter(AuthRole::getActive).flatMap(authRole -> authRole.getRole().getRolePermissions().stream()).map(RolePermission::toGrantHolder).collect(Collectors.toSet());
    }

    @Override
    public Collection<GrantHolder> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.blocked;
    }

}
