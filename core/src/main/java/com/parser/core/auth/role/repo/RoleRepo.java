package com.parser.core.auth.role.repo;

import com.parser.core.auth.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    Optional<Role> getByIdAndRdtIsNull(UUID id);

    List<Role> findAllByRdtIsNull();

    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.name = :name")
    boolean existsByName(@Param("name") String name);

}
