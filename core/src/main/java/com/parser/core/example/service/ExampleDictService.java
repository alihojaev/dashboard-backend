package com.parser.core.example.service;

import com.parser.core.example.dto.ExampleDictDto;
import com.parser.core.example.entity.ExampleDictEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExampleDictService {

    List<ExampleDictDto> listAll();

    UUID save(ExampleDictDto dto);

    UUID update(UUID id, ExampleDictDto dto);

    UUID patch(UUID id, Map<String, Object> fields);

    Page<ExampleDictDto> searchPageable(Specification<ExampleDictEntity> specification, Pageable pageable);

    ExampleDictDto getById(UUID id);

    void delete(UUID id);

}
