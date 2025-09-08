package com.parser.core.util.dao.dml;

import com.parser.core.util.dao.DaoException;
import com.parser.core.util.dao.TransactionUtil;
import com.parser.core.util.functional.BiConsumerE;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public interface SqlSelectExecutable<T> {

    T exec(Connection connection) throws SQLException;

    <A> A exec(
            Connection connection,
            Supplier<A> accumulator,
            BiConsumerE<A, T, SQLException> transfer
    ) throws SQLException;

    default T execQuietly(DataSource dataSource) {
        return TransactionUtil.transaction(
                dataSource,
                this::exec
        );
    }

    default <A extends Collection<T>> A execToCollection(
            Connection connection,
            Supplier<A> accumulator
    ) throws SQLException {
        return exec(
                connection,
                accumulator,
                Collection::add
        );
    }

    default <A extends List<T>> A execToList(Connection connection) throws SQLException {
        //noinspection unchecked
        return (A) execToCollection(
                connection,
                ArrayList::new
        );
    }

    default <A> A execQuietly(
            DataSource dataSource,
            Supplier<A> accumulator,
            BiConsumerE<A, T, SQLException> transfer
    ) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> exec(connection, accumulator, transfer)
        );
    }

    default <A extends Collection<T>> A execToCollectionBlend(
            DataSource dataSource,
            Supplier<A> accumulator
    ) {
        return execQuietly(
                dataSource,
                accumulator,
                Collection::add
        );
    }

    default <A extends List<T>> A execToListQuietly(DataSource dataSource) {
        //noinspection unchecked
        return execToCollectionBlend(
                dataSource,
                () -> (A) new ArrayList<>()
        );
    }

    default Optional<T> execToOptional(Connection connection) throws SQLException {
        return Optional.ofNullable(
                exec(
                        connection,
                        AtomicReference<T>::new,
                        (reference, value) -> {
                            if (reference.get() == null) {
                                reference.set(value);
                            } else {
                                throw new IllegalStateException("More than one element cannot be placed into Optional");
                            }
                        }
                ).get()
        );
    }

    default Optional<T> execToOptionalBlend(DataSource dataSource) {
        return TransactionUtil.transaction(dataSource, this::execToOptional);
    }

    default <A extends List<T>> A execToListBlend(DataSource dataSource, Connection connection) {
        try {
            if (connection == null) return execToListQuietly(dataSource);
            else return execToList(connection);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    default T execBlend(DataSource dataSource, Connection connection) {
        try {
            if (connection == null) return execQuietly(dataSource);
            else return exec(connection);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    default <A extends Collection<T>> A execToCollectionBlend(
            DataSource dataSource,
            Connection connection,
            Supplier<A> accumulator
    ) {
        try {
            if (connection == null) return execToCollectionBlend(dataSource, accumulator);
            else return execToCollection(connection, accumulator);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    default Optional<T> execToOptionalBlend(DataSource dataSource, Connection connection) {
        try {
            if (connection == null) return execToOptionalBlend(dataSource);
            else return execToOptional(connection);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }
}
