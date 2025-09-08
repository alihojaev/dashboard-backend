package com.parser.core.auth.permission.entity;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.auth.screen.entity.Screen;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.config.permission.AccessPermission;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Permission.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission implements IdBased {

    @SqlTable
    public static final String TABLE_NAME = "PERMISSION";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    PermissionType name;

    @Column(length = 512)
    String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    Screen screen;

    Timestamp cdt;

    public Permission(UUID id) {
        this.id = id;
    }

    public PermissionDto toModel(AccessPermission accessPermission) {
        return new PermissionDto(
                getId(),
                getName(),
                accessPermission.getAccess()
        );
    }
}
