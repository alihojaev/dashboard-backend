package com.parser.core.auth.role.entity;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.common.entity.base.BaseEntity;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Role.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends BaseEntity implements IdBased {

    @SqlTable
    public static final String TABLE_NAME = "role";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    UUID id;

    @Column(nullable = false, unique = true)
    String name;

    String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    List<RolePermission> rolePermissions;

    public Role(UUID id) {
        this.id = id;
    }

    public RoleDto toModel() {
        List<PermissionDto> permissionDtos = Collections.emptyList();

        var permissions = getRolePermissions();

        if (permissions != null) {
            permissionDtos = permissions.stream()
                    .map(p -> p.getPermission().toModel(p))
                    .collect(Collectors.toList());
        }

        return new RoleDto(
                getId(),
                getName(),
                getDescription(),
                null,
                getCdt(),
                permissionDtos
        );
    }
}
