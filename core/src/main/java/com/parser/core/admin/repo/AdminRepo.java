package com.parser.core.admin.repo;

import com.parser.core.admin.entity.AdminEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepo extends JpaRepository<AdminEntity, UUID>, JpaSpecificationExecutor<AdminEntity> {

    List<AdminEntity> findAllByRdtIsNull(Pageable pageable);

    Optional<AdminEntity> findByUsernameAndRdtIsNotNull(String username);

    Optional<AdminEntity> getByIdAndRdtIsNull(UUID id);

    Optional<AdminEntity> getByUsername(String username);
}
