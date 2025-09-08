package com.parser.core.entityFields.service;

import com.parser.core.entityFields.dto.EntityFieldInfoDto;

import java.util.List;

public interface EntityFieldsService {
    
    /**
     * Получает информацию о полях сущности
     * @param entityName название сущности
     * @return список полей с их типами и описаниями
     */
    List<EntityFieldInfoDto> getEntityFields(String entityName);
    
    /**
     * Получает список всех доступных сущностей
     * @return список названий сущностей
     */
    List<String> getAvailableEntities();
} 