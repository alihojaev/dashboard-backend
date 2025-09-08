package com.parser.core.auth.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRoleId implements Serializable {

    @Column(name = "AUTH_ID")
    UUID authId;

    @Column(name = "ROLE_ID")
    UUID roleId;
}

