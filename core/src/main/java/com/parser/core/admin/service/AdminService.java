package com.parser.core.admin.service;

import com.parser.core.admin.AdminCrateDto;
import com.parser.core.admin.AdminDto;
import com.parser.core.admin.AdminUpdatePasswordDto;
import com.parser.core.admin.dto.UserRegistrationDto;
import com.parser.core.admin.entity.AdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminService {

    List<AdminDto> getUsers(Pageable pageable);

    Page<AdminDto> searchPageable(Specification<AdminEntity> specification, Pageable pageable);

    AdminDto geById(UUID id);

    AdminEntity save(AdminCrateDto model, UUID createdBy);

    void delete(UUID authId);

    void block(UUID id);

    Optional<AdminEntity> findByUsername(String username);

    AdminDto toModel(AdminEntity auth);

    void updatePassword(AdminUpdatePasswordDto model, UUID id);

}
