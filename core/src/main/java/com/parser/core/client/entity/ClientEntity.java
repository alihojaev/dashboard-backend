package com.parser.core.client.entity;

import com.parser.core.auth.core.dto.GrantHolder;
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
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = ClientEntity.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientEntity extends BaseEntity implements UserDetails, IdBased {

    @SqlTable
    public static final String TABLE_NAME = "clients";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    UUID id;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false, unique = true)
    String username;

    String phone;

    // Поля для авторизации через email
    String password;

    // Поля для Google OAuth
    String googleId;
    String googleEmail;



    // Статус аккаунта
    Boolean blocked = false;

    // Тип авторизации
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AuthType authType;

    LocalDateTime lastActivity;

    @Transient
    transient Collection<GrantHolder> authorities = Collections.emptySet();

    public enum AuthType {
        EMAIL, GOOGLE
    }

    public ClientEntity(UUID id) {
        this.id = id;
    }

    public ClientEntity(UUID id,
                       String email,
                       String username,
                       String phone,
                       String password,
                       String googleId,
                       String googleEmail,
                       Boolean blocked,
                       AuthType authType,
                       LocalDateTime lastActivity,
                       UUID createdBy,
                       UUID modifiedBy,
                       LocalDateTime cdt,
                       LocalDateTime mdt,
                       LocalDateTime rdt) {
        super(createdBy, modifiedBy, cdt, mdt, rdt);
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.googleId = googleId;
        this.googleEmail = googleEmail;
        this.blocked = blocked;
        this.authType = authType;
        this.lastActivity = lastActivity;
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

    // Методы для проверки типа авторизации
    public boolean isEmailAuth() {
        return AuthType.EMAIL.equals(this.authType);
    }

    public boolean isGoogleAuth() {
        return AuthType.GOOGLE.equals(this.authType);
    }

    // Методы для получения email в зависимости от типа авторизации
    public String getPrimaryEmail() {
        switch (this.authType) {
            case GOOGLE:
                return this.googleEmail != null ? this.googleEmail : this.email;
            default:
                return this.email;
        }
    }
}
