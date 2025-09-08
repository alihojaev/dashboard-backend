package com.parser.core.report.util;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import com.parser.core.exceptions.BadRequestException;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import com.parser.core.util.rsql.RsqlSpecificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryUtil {
    private static final Logger log = LoggerFactory.getLogger(RepositoryUtil.class);

    public static JpaRepository<?, ?> getRepository(ApplicationContext ctx, String entityName) {
        // Пример: ищем бин по имени (ExampleDictEntity -> exampleDictRepo)
        String repoBeanName = Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1, entityName.length() - "Entity".length()) + "Repo";
        return (JpaRepository<?, ?>) ctx.getBean(repoBeanName);
    }

    public static List<?> getAllData(JpaRepository<?, ?> repo) {
        return repo.findAll();
    }

    /**
     * Получает данные с RSQL фильтрацией
     */
    @SuppressWarnings("unchecked")
    public static List<?> getDataWithRsqlFilter(JpaRepository<?, ?> repository, String rsqlFilter) {
        if (rsqlFilter == null || rsqlFilter.trim().isEmpty()) {
            return getAllData(repository);
        }
        if (!supportsSpecifications(repository)) {
            log.warn("Репозиторий не поддерживает спецификации, возвращаются все данные");
            return getAllData(repository);
        }
        try {
            RsqlSpecificationFactory<Object> factory = new RsqlSpecificationFactory<>();
            Specification<Object> spec = factory.createSpecification(rsqlFilter);
            if (spec != null) {
                return ((JpaSpecificationExecutor<Object>) repository).findAll(spec);
            } else {
                return getAllData(repository);
            }
        } catch (Exception e) {
            log.error("Ошибка при применении RSQL фильтра: {}", e.getMessage(), e);
            throw new BadRequestException("Ошибка при применении RSQL фильтра: " + e.getMessage());
        }
    }

    private static boolean supportsSpecifications(JpaRepository<?, ?> repository) {
        return repository instanceof JpaSpecificationExecutor;
    }
} 