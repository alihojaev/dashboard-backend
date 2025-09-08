package com.parser.core.files.repo;

import com.parser.core.files.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepo extends JpaRepository<FileEntity, UUID>, JpaSpecificationExecutor<FileEntity> {

    List<FileEntity> findAllByRdtIsNull();

    Optional<FileEntity> findByNameAndRdtIsNull(String name);
}
