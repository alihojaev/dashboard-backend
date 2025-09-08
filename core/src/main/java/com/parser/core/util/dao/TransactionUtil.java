package com.parser.core.util.dao;

import com.parser.core.exceptions.BadRequestException;
import com.parser.core.util.functional.ConsumerE;
import com.parser.core.util.functional.FunctionE;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class TransactionUtil {

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (Exception e) {
                log.error("rollback()", e);
            }
        }
    }

    public static <T> T transaction(
            DataSource dataSource,
            FunctionE<Connection, T, SQLException> action,
            Function<Exception, T> onError
    ) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            T result = action.apply(connection);
            connection.commit();
            return result;
        } catch (Exception e) {
            log.error("transaction()", e);
            rollback(connection);

            if (e instanceof RuntimeException) {
                throw new BadRequestException(e.getMessage());
            }

            return onError.apply(e);
        } finally {
            Closer.close(connection);
        }
    }

    public static <T> T transaction(
            DataSource dataSource,
            FunctionE<Connection, T, SQLException> action
    ) {
        return transaction(dataSource, action, e -> {
            log.error("{}", e);
            throw new BadRequestException(e.getMessage());
        });
    }

    public static void transactionNoRet(
            DataSource dataSource,
            ConsumerE<Connection, SQLException> action,
            Consumer<Exception> onError
    ) {
        transaction(
                dataSource,
                connection -> {
                    action.accept(connection);
                    return Void.TYPE;
                },
                e -> {
                    onError.accept(e);
                    return Void.TYPE;
                }
        );
    }

    public static void transactionNoRet(
            DataSource dataSource,
            ConsumerE<Connection, SQLException> action
    ) {
        transaction(dataSource, c -> {
            action.accept(c);
            return Void.TYPE;
        });
    }
}

