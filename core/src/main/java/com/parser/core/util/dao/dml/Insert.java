package com.parser.core.util.dao.dml;

import com.parser.core.util.dao.Closer;
import com.parser.core.util.dao.DaoException;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.dao.TransactionUtil;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.SupplierE;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Insert extends SqlBuilder<Insert> {

    String returning = "id";

    public Insert(String sql, List<BiConsumerE<PreparedStatement, Integer, SQLException>> values) {
        super(sql, values);
    }

    public InsertNoReturning notReturning() {
        return new InsertNoReturning(sql, statementConsumer, values);
    }

    public Insert returning(@NonNull String returning) {
        this.returning = returning;
        return this;
    }

    public UUID exec(Connection connection) throws SQLException {
        try (Closer closer = new Closer()) {
            PreparedStatement statement = closer.reg(
                    returning == null ? connection.prepareStatement(sql) : connection.prepareStatement(sql, new String[]{returning})
            );

            statementConsumer.accept(statement);

            statement.executeUpdate();

            ResultSet key = closer.reg(statement.getGeneratedKeys());

            if (key.next()) {
                return UUID.fromString(key.getString(1));
            } else {
                return null;
            }
        }
    }

    public UUID execAndReturnUUID(Connection connection) throws SQLException {
        try (Closer closer = new Closer()) {
            PreparedStatement statement = closer.reg(
                    returning == null ? connection.prepareStatement(sql) : connection.prepareStatement(sql, new String[]{returning})
            );

            statementConsumer.accept(statement);

            int affected = statement.executeUpdate();

            ResultSet key = closer.reg(statement.getGeneratedKeys());

            if (key.next()) {
                return UUID.fromString(key.getString(1));
            } else {
                throw new SQLException("JDBC not responded");
            }
        }
    }


    public UUID execWithoutClosing(Connection connection) throws SQLException {
        Closer closer = new Closer();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            PreparedStatement statement = closer.reg(returning == null
                    ? connection.prepareStatement(sql)
                    : connection.prepareStatement(sql, new String[]{returning})
            );

            statementConsumer.accept(statement);

            int affected = statement.executeUpdate();
            if (affected == 0) throw new SQLException("Could not insert");

            ResultSet key = closer.reg(statement.getGeneratedKeys());

            if (key.next()) {
                return UUID.fromString(key.getString(1));
            } else {
                throw new SQLException("JDBC not responded");
            }
        } finally {
            closer.close();
        }
    }

    public <T> void execBatch(
            Connection connection,
            SupplierE<Iterable<T>, SQLException> dataSupplier,
            BiConsumerE<PreparedStatement, T, SQLException> dataMapper,
            BiConsumerE<T, UUID, SQLException> returningConsumer
    ) throws SQLException {
        for (T entity : dataSupplier.get()) {
            this.statementConsumer = statement -> dataMapper.accept(statement, entity);
            UUID id = exec(connection);
            if (returningConsumer != null) returningConsumer.accept(entity, id);
        }
    }

    public UUID execQuietly(DataSource dataSource) {
        return TransactionUtil.transaction(
                dataSource,
                this::exec
        );
    }

    public UUID execQuietlyAndReturnUUID(DataSource dataSource) {
        return TransactionUtil.transaction(
                dataSource,
                this::execAndReturnUUID
        );
    }

    public <T> void execBatchQuietly(
            DataSource dataSource,
            SupplierE<Iterable<T>, SQLException> dataSupplier,
            BiConsumerE<PreparedStatement, T, SQLException> dataMapper,
            BiConsumerE<T, UUID, SQLException> returningConsumer
    ) {
        TransactionUtil.transactionNoRet(
                dataSource,
                connection -> {
                    execBatch(
                            connection,
                            dataSupplier,
                            dataMapper,
                            returningConsumer
                    );
                }
        );
    }

    public UUID execBlend(DataSource dataSource, Connection connection) {
        try {
            if (connection == null) return execQuietly(dataSource);
            else return exec(connection);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }
}
