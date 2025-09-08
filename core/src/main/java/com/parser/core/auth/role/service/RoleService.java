package com.parser.core.auth.role.service;

import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.common.dto.IdNameDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    List<RoleDto> listAllAsModel();

    List<IdNameDto> listAllMin();

    Optional<Role> findById(UUID id);

    void save(RoleDto model);

    void delete(UUID roleId);

    List<RoleDto> listAllByRdtIsNullAsModel();

    List<RoleDto> findAllRsql(Specification<Role> specification);

    Page<RoleDto> searchPageable(Specification<Role> spec, Pageable pageable);
}
