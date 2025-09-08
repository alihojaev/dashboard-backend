package com.parser.core.example.repo;

import com.parser.core.example.entity.ExampleDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ExampleDictRepo extends JpaRepository<ExampleDictEntity, UUID>, JpaSpecificationExecutor<ExampleDictEntity> {

    List<ExampleDictEntity> findAllByRdtIsNull();
}
