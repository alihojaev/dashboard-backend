package com.parser.core.common.jdbc;

import com.parser.core.common.entity.base.DaoRequestContext;
import com.parser.core.common.entity.base.IdBased;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DaoIdBasedContext<E extends IdBased, D extends EntityDao<E>> extends DaoRequestContext<D> {

    D dao;
    Map<UUID, E> entities;

    public DaoIdBasedContext(Connection connection, D dao) {
        super(connection);
        this.dao = dao;
        entities = new HashMap<>();
    }

    public E getById(UUID id) throws SQLException {
        if (id != null) {
            E e = entities.get(id);

            if (e == null) {
                e = dao.getById(id, getConnection()).orElse(null);
                entities.put(id, e);
            }

            return e;
        } else {
            return null;
        }
    }
}
