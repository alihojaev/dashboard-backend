package com.parser.core.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parser.core.example.dto.ExampleDictDto;
import com.parser.core.example.entity.ExampleDictEntity;
import com.parser.core.example.mapper.ExampleMapper;
import com.parser.core.example.repo.ExampleDictRepo;
import com.parser.core.exceptions.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExampleDictServiceImpl implements ExampleDictService {

    ExampleDictRepo repo;
    ExampleMapper exampleMapper;
    ObjectMapper objectMapper;

    @Override
    public UUID save(ExampleDictDto dto) {
        ExampleDictEntity entity = exampleMapper.toEntity(dto);
        return repo.save(entity).getId();
    }

    @Override
    public UUID update(UUID id, ExampleDictDto dto) {
        ExampleDictEntity entity = exampleMapper.toEntity(dto);
        entity.setId(id);
        return repo.save(entity).getId();
    }

    @Override
    @SneakyThrows
    @Transactional
    public UUID patch(UUID id, Map<String, Object> fields) {
        var entity = getById(id);

        fields.forEach((key, value) -> {
            if (value == null || value.toString().isBlank()) {
                throw new BadRequestException("Field '" + key + "' must not be blank");
            }
        });

        ExampleDictDto updated = objectMapper.updateValue(entity, fields);

        return repo.save(exampleMapper.toEntity(updated)).getId();
    }

    @Override
    public List<ExampleDictDto> listAll() {
        return exampleMapper.toDtoList(repo.findAllByRdtIsNull());
    }

    @Override
    public Page<ExampleDictDto> searchPageable(Specification<ExampleDictEntity> specification, Pageable pageable) {
        return repo.findAll(specification, pageable).map(exampleMapper::toDto);
    }

    @Override
    public ExampleDictDto getById(UUID id) {
        return exampleMapper.toDto(findById(id));
    }

    private ExampleDictEntity findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new BadRequestException("record not found"));
    }


    @Override
    public void delete(UUID id) {
        ExampleDictEntity entity = findById(id);
        entity.setRdt(LocalDateTime.now());
        repo.save(entity);
    }


}