package com.parser.core.util.validator;

import com.parser.core.auth.role.entity.Role;
import com.parser.core.util.annotation.UniqueNameWithRdt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueNameWithRdtValidator implements ConstraintValidator<UniqueNameWithRdt, Role> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        if (role.getRdt() == null) {
            String query = "SELECT COUNT(r) FROM Role r WHERE r.name = :name AND r.rdt IS NULL";
            Long count = entityManager.createQuery(query, Long.class)
                    .setParameter("name", role.getName())
                    .getSingleResult();
            return count == 0;
        }
        return true;
    }
}
