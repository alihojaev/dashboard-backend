package com.parser.core.common.jdbc;

import com.parser.core.common.entity.base.DaoRequestContext;
import com.parser.core.util.dao.TransactionUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseDao<E, C extends DaoRequestContext<? extends Dao<E>>> implements Dao<E> {

    protected DataSource dataSource;

    protected abstract C createContext(Connection connection);

    public abstract E map(ResultSet rs, C context) throws SQLException;

    @Override
    public Optional<E> save(E e) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> Optional.of(save(e, connection))
        );
    }

    @Override
    public List<E> saveAll(Collection<E> collection, Connection connection) throws SQLException {
        List<E> result = new ArrayList<>(collection.size());

        for (E e : collection) {
            result.add(save(e, connection));
        }

        return result;
    }

    @Override
    public List<E> saveAll(Collection<E> collection) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> saveAll(collection, connection)
        );
    }

    @Override
    public boolean delete(E e) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> {
                    delete(e, connection);
                    return true;
                }
        );
    }

    @Override
    public void deleteAll(Collection<E> collection, Connection connection) throws SQLException {
        for (E e : collection) {
            delete(e, connection);
        }
    }

    @Override
    public boolean deleteAll(Collection<E> collection) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> {
                    deleteAll(collection, connection);
                    return true;
                }
        );
    }
}