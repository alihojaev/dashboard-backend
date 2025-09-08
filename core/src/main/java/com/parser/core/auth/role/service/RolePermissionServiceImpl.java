package com.parser.core.auth.role.service;

import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.auth.role.repo.RolePermissionRepo;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RolePermissionServiceImpl implements RolePermissionService {

    RolePermissionRepo repo;

    @Override
    public void saveAll(List<RolePermission> rolePermissions) {
        repo.saveAll(rolePermissions);
    }

    @Override
    public void deleteAll(List<RolePermission> rolePermissions) {
        repo.deleteAll(rolePermissions);
    }
}
