package com.parser.core.files.mapper;

import com.parser.core.files.dto.FileDto;
import com.parser.core.files.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FileMapper {

    FileDto toDto(FileEntity entity);

    FileEntity toEntity(FileDto dto);

    List<FileDto> toDtoList(List<FileEntity> entity);

    void updateDto(@MappingTarget FileEntity entity, FileDto dto);

}
