package com.parser.core.entityFields.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityFieldInfoDto {
    
    /**
     * Название поля (например, "id", "name", "employee.name")
     */
    private String fieldName;
    
    /**
     * Тип поля (например, "String", "Integer", "LocalDate", "UUID")
     */
    private String fieldType;
    
    private String description;
    
    /**
     * Конструктор для простых случаев
     */
    public EntityFieldInfoDto(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
} 