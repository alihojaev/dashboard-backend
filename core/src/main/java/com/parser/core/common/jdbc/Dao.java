package com.parser.core.common.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public interface Dao<E> {

    Optional<E> getById(UUID id);

    Optional<E> getById(UUID id, Connection connection) throws SQLException;

    Optional<E> save(E e);

    E save(E e, Connection connection) throws SQLException;

    List<E> saveAll(Collection<E> collection);

    List<E> saveAll(Collection<E> collection, Connection connection) throws SQLException;

    boolean delete(E e);

    void delete(E e, Connection connection) throws SQLException;

    boolean deleteAll(Collection<E> collection);

    void deleteAll(Collection<E> collection, Connection connection) throws SQLException;
}
