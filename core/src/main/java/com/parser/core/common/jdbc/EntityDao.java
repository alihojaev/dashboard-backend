package com.parser.core.common.jdbc;


import com.parser.core.common.entity.base.IdBased;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public interface EntityDao<E extends IdBased> extends Dao<E> {

    UUID insert(E e, Connection connection) throws SQLException;

    void update(E e, Connection connection) throws SQLException;
}
