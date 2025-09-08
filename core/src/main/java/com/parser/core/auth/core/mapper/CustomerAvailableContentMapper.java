package com.parser.core.auth.core.mapper;

import com.parser.core.auth.core.dto.CustomerAvailableContentDto;
import com.parser.core.auth.core.entity.CustomerAvailableContent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CustomerAvailableContentMapper {

    CustomerAvailableContent toEntity(CustomerAvailableContentDto dto);

    CustomerAvailableContentDto toDto(CustomerAvailableContent entity);

    List<CustomerAvailableContentDto> toDtoList(List<CustomerAvailableContent> entity);

    void updateDto(@MappingTarget CustomerAvailableContent entity, CustomerAvailableContentDto dto);

}
