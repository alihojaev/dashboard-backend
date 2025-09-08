package com.parser.core.client.mapper;

import com.parser.core.client.dto.ClientDto;
import com.parser.core.client.dto.ClientRegistrationDto;
import com.parser.core.client.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "cdt", ignore = true)
    @Mapping(target = "mdt", ignore = true)
    @Mapping(target = "rdt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "lastActivity", ignore = true)
    @Mapping(target = "blocked", constant = "false")
    ClientEntity toEntity(ClientRegistrationDto dto);
    
    ClientDto toDto(ClientEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "cdt", ignore = true)
    @Mapping(target = "mdt", ignore = true)
    @Mapping(target = "rdt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "lastActivity", ignore = true)
    void updateEntityFromDto(ClientDto dto, @org.mapstruct.MappingTarget ClientEntity entity);
} 