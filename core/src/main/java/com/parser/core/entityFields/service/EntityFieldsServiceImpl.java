package com.parser.core.entityFields.service;

import com.parser.core.entityFields.dto.EntityFieldInfoDto;
import com.parser.core.entityFields.util.FieldDescriptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class EntityFieldsServiceImpl implements EntityFieldsService {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public List<EntityFieldInfoDto> getEntityFields(String entityName) {
        List<EntityFieldInfoDto> fields = new ArrayList<>();
        
        try {
            // Получаем метамодель JPA
            Metamodel metamodel = entityManager.getMetamodel();
            
            // Ищем сущность по имени
            EntityType<?> entityType = findEntityType(metamodel, entityName);
            if (entityType == null) {
                log.warn("Сущность с именем '{}' не найдена", entityName);
                return fields;
            }
            
            // Получаем поля через рефлексию
            Class<?> entityClass = entityType.getJavaType();
            addFieldsFromClass(fields, entityClass, "");
            
        } catch (Exception e) {
            log.error("Ошибка при получении полей сущности '{}': {}", entityName, e.getMessage(), e);
        }
        
        return fields;
    }
    
    @Override
    public List<String> getAvailableEntities() {
        List<String> entities = new ArrayList<>();
        
        try {
            Metamodel metamodel = entityManager.getMetamodel();
            Set<EntityType<?>> entityTypes = metamodel.getEntities();
            
            for (EntityType<?> entityType : entityTypes) {
                entities.add(entityType.getName());
            }
            
            // Сортируем по алфавиту для удобства
            entities.sort(String::compareToIgnoreCase);
            
        } catch (Exception e) {
            log.error("Ошибка при получении списка сущностей: {}", e.getMessage(), e);
        }
        
        return entities;
    }
    
    /**
     * Ищет тип сущности по имени
     */
    private EntityType<?> findEntityType(Metamodel metamodel, String entityName) {
        for (EntityType<?> entityType : metamodel.getEntities()) {
            if (entityType.getName().equalsIgnoreCase(entityName) || 
                entityType.getJavaType().getSimpleName().equalsIgnoreCase(entityName)) {
                return entityType;
            }
        }
        return null;
    }
    
    /**
     * Добавляет поля из класса через рефлексию
     */
    private void addFieldsFromClass(List<EntityFieldInfoDto> fields, Class<?> clazz, String prefix) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        
        // Получаем все поля класса
        Field[] declaredFields = clazz.getDeclaredFields();
        
        for (Field field : declaredFields) {
            // Пропускаем статические и синтетические поля
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || 
                field.isSynthetic()) {
                continue;
            }
            
            String fieldName = field.getName();
            
            // Пропускаем поля паролей и служебные поля
            if ("password".equalsIgnoreCase(fieldName) ||
                "rdt".equalsIgnoreCase(fieldName) ||
                "mdt".equalsIgnoreCase(fieldName) ||
                "createdBy".equalsIgnoreCase(fieldName) ||
                "modifiedBy".equalsIgnoreCase(fieldName)) {
                continue;
            }
            
            String fullFieldName = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;
            String fieldType = getFieldTypeName(field.getType());
            String description = FieldDescriptionUtil.getFieldDescription(fullFieldName);
            
            fields.add(new EntityFieldInfoDto(fullFieldName, fieldType, description));
            
            // Рекурсивно добавляем поля связанной сущности
            if (isEntityType(field.getType())) {
                addFieldsFromClass(fields, field.getType(), fullFieldName);
            }
        }
        
        // Рекурсивно обрабатываем родительский класс
        addFieldsFromClass(fields, clazz.getSuperclass(), prefix);
    }
    
    /**
     * Получает название типа поля
     */
    private String getFieldTypeName(Class<?> type) {
        if (type == null) {
            return "Unknown";
        }
        
        // Простые типы
        if (type == String.class) return "String";
        if (type == Integer.class || type == int.class) return "Integer";
        if (type == Long.class || type == long.class) return "Long";
        if (type == Double.class || type == double.class) return "Double";
        if (type == Float.class || type == float.class) return "Float";
        if (type == Boolean.class || type == boolean.class) return "Boolean";
        if (type == java.time.LocalDate.class) return "LocalDate";
        if (type == java.time.LocalDateTime.class) return "LocalDateTime";
        if (type == java.time.LocalTime.class) return "LocalTime";
        if (type == java.util.Date.class) return "Date";
        if (type == java.util.UUID.class) return "UUID";
        if (type == java.math.BigDecimal.class) return "BigDecimal";
        if (type == java.math.BigInteger.class) return "BigInteger";
        
        // Массивы
        if (type.isArray()) {
            return getFieldTypeName(type.getComponentType()) + "[]";
        }
        
        // Коллекции
        if (java.util.Collection.class.isAssignableFrom(type)) {
            return "Collection";
        }
        
        // Enum
        if (type.isEnum()) {
            return "Enum";
        }
        
        // Остальные типы
        return type.getSimpleName();
    }
    
    /**
     * Проверяет, является ли тип сущностью
     */
    private boolean isEntityType(Class<?> type) {
        if (type == null) {
            return false;
        }
        
        // Проверяем наличие аннотации @Entity
        return type.isAnnotationPresent(jakarta.persistence.Entity.class);
    }
} 