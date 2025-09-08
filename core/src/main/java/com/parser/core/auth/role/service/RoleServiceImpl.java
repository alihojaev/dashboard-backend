package com.parser.core.auth.role.service;

import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.auth.role.mapper.RoleMapper;
import com.parser.core.auth.role.repo.RoleRepo;
import com.parser.core.common.dto.IdNameDto;
import com.parser.core.exceptions.ValidationException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepo roleRepo;
    RoleMapper roleMapper;
    RolePermissionService rolePermissionService;

    @Override
    @Transactional
    public List<RoleDto> listAllAsModel() {
        return roleMapper.toDtoList(roleRepo.findAllByRdtIsNull());
    }

    @Override
    public List<IdNameDto> listAllMin() {
        return null;
    }

    @Override
    public Optional<Role> findById(UUID roleId) {
        return roleRepo.findById(roleId);
    }

    @Override
    @Transactional
    public void save(RoleDto model) {
        model.validate();

        if (model.getId() == null && roleRepo.existsByName(model.getName()))
            throw new ValidationException("Роль c именем " + model.getName() + " уже существует");

        Role role;

        if (model.getId() == null) {
            role = new Role();
        } else {
            role = roleRepo.getByIdAndRdtIsNull(model.getId())
                    .orElseThrow(() -> new ValidationException(
                            "Запись с id = " + model.getId() + " не найдена"));

            rolePermissionService.deleteAll(role.getRolePermissions());
            role.setRolePermissions(null);
        }

        role.setName(model.getName());
        role.setDescription(model.getDescription());

        roleRepo.save(role);

        rolePermissionService.saveAll(
                model.getPermissions().stream()
                        .map(pm -> new RolePermission(
                                role,
                                new Permission(pm.getId()),
                                15
                        ))
                        .collect(Collectors.toList()));
    }

    @Override
    public void delete(UUID roleId) {
        roleRepo.getByIdAndRdtIsNull(roleId)
                .ifPresent(role -> {
                    role.markRemoved();
                    role.setRolePermissions(null);
                    roleRepo.save(role);
                });
    }

    @Override
    public List<RoleDto> listAllByRdtIsNullAsModel() {
        return null;
    }

    @Override
    @Transactional
    public List<RoleDto> findAllRsql(Specification<Role> specification) {
        return roleMapper.toDtoList(roleRepo.findAll(specification));
    }

    @Override
    @Transactional
    public Page<RoleDto> searchPageable(Specification<Role> specification, Pageable pageable) {
        return roleRepo.findAll(specification, pageable).map(roleMapper::toDto);
    }
}
