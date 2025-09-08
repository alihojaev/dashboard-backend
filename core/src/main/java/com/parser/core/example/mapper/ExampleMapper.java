package com.parser.core.example.mapper;

import com.parser.core.example.dto.ExampleDictDto;
import com.parser.core.example.entity.ExampleDictEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ExampleMapper {

    ExampleDictEntity toEntity(ExampleDictDto dto);

    ExampleDictDto toDto(ExampleDictEntity entity);

    List<ExampleDictDto> toDtoList(List<ExampleDictEntity> entity);

    void updateDto(@MappingTarget ExampleDictEntity entity, ExampleDictDto dto);

}
