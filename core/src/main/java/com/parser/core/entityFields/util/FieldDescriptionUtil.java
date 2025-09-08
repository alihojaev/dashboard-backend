package com.parser.core.entityFields.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FieldDescriptionUtil {
    
    private static Map<String, String> fieldDescriptions;
    
    static {
        loadFieldDescriptions();
    }
    
    /**
     * Загружает описания полей из JSON файла
     */
    private static void loadFieldDescriptions() {
        fieldDescriptions = new HashMap<>();
        try {
            ClassPathResource resource = new ClassPathResource("field-descriptions.json");
            try (InputStream inputStream = resource.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(inputStream);
                JsonNode descriptionsNode = rootNode.get("fieldDescriptions");
                
                if (descriptionsNode != null && descriptionsNode.isObject()) {
                    descriptionsNode.fields().forEachRemaining(entry -> {
                        fieldDescriptions.put(entry.getKey(), entry.getValue().asText());
                    });
                }
            }
        } catch (IOException e) {
            log.warn("Не удалось загрузить файл с описаниями полей: {}", e.getMessage());
        }
    }
    
    /**
     * Получает описание поля по его названию
     * @param fieldName название поля (например, "employee.category.name")
     * @return описание поля или null, если описание не найдено
     */
    public static String getFieldDescription(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }
        
        // Сначала ищем точное совпадение
        String description = fieldDescriptions.get(fieldName);
        if (description != null) {
            return description;
        }
        
        // Если точное совпадение не найдено, пытаемся сгенерировать описание
        return generateFieldDescription(fieldName);
    }
    
    /**
     * Генерирует описание поля на основе его названия
     * @param fieldName название поля
     * @return сгенерированное описание
     */
    private static String generateFieldDescription(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = fieldName.split("\\.");
        if (parts.length == 0) {
            return fieldName;
        }
        
        StringBuilder description = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // Получаем описание для части поля
            String partDescription = getPartDescription(part);
            
            if (partDescription != null) {
                if (description.length() > 0) {
                    description.append("-");
                }
                description.append(partDescription);
            } else {
                // Если описание не найдено, используем оригинальное название
                if (description.length() > 0) {
                    description.append("-");
                }
                description.append(part);
            }
        }
        
        return description.toString();
    }
    
    /**
     * Получает описание части поля
     * @param part часть поля
     * @return описание части
     */
    private static String getPartDescription(String part) {
        // Сначала ищем в загруженных описаниях
        String description = fieldDescriptions.get(part);
        if (description != null) {
            return description;
        }
        
        // Если не найдено, используем fallback маппинг
        switch (part.toLowerCase()) {
            case "id":
                return "идентификатор";
            case "name":
                return "название";
            case "employee":
                return "сотрудник";
            case "category":
                return "категория";
            case "jobs":
                return "должность";
            case "direction":
                return "направление";
            case "route":
                return "маршрут";
            case "city":
                return "город";
            case "country":
                return "страна";
            case "owner":
                return "владелец";
            case "number":
                return "номер";
            case "date":
                return "дата";
            case "time":
                return "время";
            case "description":
                return "описание";
            case "phone":
                return "телефон";
            case "email":
                return "email";
            case "address":
                return "адрес";
            case "color":
                return "цвет";
            case "brand":
                return "марка";
            case "model":
                return "модель";
            case "cost":
                return "стоимость";
            case "length":
                return "длина";
            case "width":
                return "ширина";
            case "height":
                return "высота";
            case "weight":
                return "масса";
            case "capacity":
                return "емкость";
            case "power":
                return "мощность";
            case "registration":
                return "регистрация";
            case "certificate":
                return "свидетельство";
            case "passport":
                return "паспорт";
            case "license":
                return "лицензия";
            case "photo":
                return "фото";
            case "file":
                return "файл";
            case "type":
                return "тип";
            case "extension":
                return "расширение";
            case "hash":
                return "хеш";
            case "comment":
                return "комментарий";
            case "surname":
                return "фамилия";
            case "patronymic":
                return "отчество";
            case "birth":
                return "рождение";
            case "issue":
                return "выдача";
            case "expiration":
                return "окончание";
            case "citizenship":
                return "гражданство";
            case "directorate":
                return "дирекция";
            case "group":
                return "группа";
            case "subgroup":
                return "подгруппа";
            case "schedule":
                return "расписание";
            case "position":
                return "позиция";
            case "first":
                return "первая";
            case "second":
                return "вторая";
            case "side":
                return "сторона";
            case "face":
                return "лицо";
            case "border":
                return "загран";
            case "composition":
                return "состав";
            case "identification":
                return "идентификационный";
            case "ru":
                return "рус";
            default:
                return part;
        }
    }
} 