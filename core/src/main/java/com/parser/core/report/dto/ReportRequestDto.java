package com.parser.core.report.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReportRequestDto {
    private String entityName; // Имя сущности (например, ExampleDictEntity)
    private List<String> fields; // Список полей для выгрузки
    private Map<String, String> headers; // Кастомные заголовки для CSV (опционально)
    private String rsqlFilter; // RSQL фильтр (опционально)
    private Map<String, Object> filters; // Простые фильтры (опционально)
    private List<Map<String, Object>> advancedFilters; // Расширенные фильтры (опционально)
} 