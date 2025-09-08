package com.parser.core.admin.service;

import com.lambdaworks.crypto.SCryptUtil;
import com.parser.core.admin.AdminCrateDto;
import com.parser.core.admin.AdminDto;
import com.parser.core.admin.AdminUpdatePasswordDto;
import com.parser.core.admin.dto.UserRegistrationDto;
import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.admin.mapper.AdminMapper;
import com.parser.core.admin.repo.AdminRepo;
import com.parser.core.auth.core.entity.AuthRole;
import com.parser.core.auth.core.entity.AuthRoleId;
import com.parser.core.auth.core.repo.AuthRoleRepo;
import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.auth.role.mapper.RoleMapper;
import com.parser.core.exceptions.BadRequestException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminServiceImpl implements AdminService {

    AdminRepo repo;
    AdminMapper adminMapper;
    AuthRoleRepo authRoleRepo;
    RoleMapper roleMapper;

    @Override
    @Transactional
    public List<AdminDto> getUsers(Pageable pageable) {
        return adminMapper.toDtoList(repo.findAllByRdtIsNull(pageable));
    }

    @Override
    @Transactional
    public Page<AdminDto> searchPageable(Specification<AdminEntity> specification, Pageable pageable) {
        return repo.findAll(specification, pageable).map(adminMapper::toDto);
    }

    @Override
    @Transactional
    public AdminDto geById(UUID id) {
        return adminMapper.toDto(repo.getByIdAndRdtIsNull(id).orElseThrow(() -> new BadRequestException("User not found")));
    }

    @Override
    @Transactional
    public AdminEntity save(AdminCrateDto dto, UUID createdBy) {

        var exist =
                dto.getId() == null ?
                        repo.getByUsername(dto.getUsername()) :
                        repo.findById(dto.getId());
        if (exist.isPresent() && !exist.get().getId().equals(dto.getId())) {
            throw new BadRequestException("Пользователь с таким username уже существует");
        }

        AdminEntity entity = adminMapper.toCreateEntity(dto);

        entity.setPassword(exist.isPresent() ? exist.get().getPassword() : SCryptUtil.scrypt(entity.getPassword(), 16, 16, 16));
        entity.setCreatedBy(createdBy);
        entity.setModifiedBy(createdBy);
        entity.setCdt(LocalDateTime.now());
        entity.setMdt(LocalDateTime.now());
        entity.setLastActivity(LocalDateTime.now());

        var user = repo.save(entity);

        if (dto.getAuthRoles() != null && !dto.getAuthRoles().isEmpty()) {
            exist.ifPresent(users -> authRoleRepo.deleteAllByAuth(user));
            for (var role : dto.getAuthRoles()) {
                authRoleRepo.save(new AuthRole(
                        new AuthRoleId(
                                user.getId(),
                                role.getId()
                        ),
                        user,
                        roleMapper.toEntity(role),
                        true
                ));
            }
        }

        return user;
    }

    private AuthRole mapSingleRoleDto(RoleDto roleDto) {
        List<RolePermission> rolePermissions = new ArrayList<>();
        for (var permission : roleDto.getPermissions()) {
            rolePermissions.add(new RolePermission(
                    new Role(
                            roleDto.getId(),
                            roleDto.getName(),
                            roleDto.getDescription(),
                            null
                    ),
                    new Permission(
                            permission.getId(),
                            permission.getName(),
                            permission.fromModel().getDescription(),
                            permission.fromModel().getScreen(),
                            permission.fromModel().getCdt()
                    ),
                    15
            ));
        }

        AuthRole authRole = new AuthRole();
        authRole.setAuth(new AdminEntity());
        authRole.setRole(new Role(
                roleDto.getId(),
                roleDto.getName(),
                roleDto.getDescription(),
                rolePermissions
        ));

        return authRole;
    }

    @Override
    public void delete(UUID authId) {
        var entity = repo.getByIdAndRdtIsNull(authId).orElseThrow(() -> new BadRequestException("User not found"));
        entity.setRdt(LocalDateTime.now());
        repo.save(entity);
    }

    @Override
    public void block(UUID id) {
        var entity = repo.getByIdAndRdtIsNull(id).orElseThrow(() -> new BadRequestException("User not found"));
        entity.setBlocked(true);
        repo.save(entity);
    }

    @Override
    public Optional<AdminEntity> findByUsername(String username) {
        return repo.findByUsernameAndRdtIsNotNull(username);
    }

    @Override
    @Transactional
    public AdminDto toModel(AdminEntity auth) {
        return adminMapper.toDto(auth);
    }

    @Override
    public void updatePassword(AdminUpdatePasswordDto model, UUID id) {
        if (model.getId().equals(id)) throw new BadRequestException("Нельзя меня пароль самому себе");
        var user = repo.findById(id).orElseThrow(() -> new BadRequestException("Пользователь не найден"));

        user.setPassword(SCryptUtil.scrypt(model.getPassword(), 16, 16, 16));
        user.setModifiedBy(id);
        user.setMdt(LocalDateTime.now());

        repo.save(user);
    }
}
