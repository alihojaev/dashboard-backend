package com.parser.core.util.dao.dml;

import com.parser.core.util.dao.Closer;
import com.parser.core.util.dao.DaoException;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.dao.TransactionUtil;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.ConsumerE;
import com.parser.core.util.functional.SupplierE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class InsertNoReturning extends SqlBuilder<InsertNoReturning> {

    InsertNoReturning(
            String sql,
            ConsumerE<PreparedStatement, SQLException> statementConsumer,
            List<BiConsumerE<PreparedStatement, Integer, SQLException>> values
    ) {
        super(sql, values);
        this.statementConsumer = statementConsumer;
    }

    public void exec(Connection connection) throws SQLException {
        try (Closer closer = new Closer()) {
            PreparedStatement statement = closer.reg(connection.prepareStatement(sql));

            statementConsumer.accept(statement);

            int affected = statement.executeUpdate();
            if (affected == 0) throw new SQLException("Could not insert");
        }
    }


    public void execWithoutClosing(Connection connection) throws SQLException {
        Closer closer = new Closer();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            PreparedStatement statement = closer.reg(connection.prepareStatement(sql));

            statementConsumer.accept(statement);

            int affected = statement.executeUpdate();
            if (affected == 0) throw new SQLException("Could not insert");
        } finally {
            closer.close();
        }
    }

    public <T> void execBatch(
            Connection connection,
            SupplierE<Iterable<T>, SQLException> dataSupplier,
            BiConsumerE<PreparedStatement, T, SQLException> dataMapper
    ) throws SQLException {
        for (T entity : dataSupplier.get()) {
            this.statementConsumer = statement -> dataMapper.accept(statement, entity);
            exec(connection);
        }
    }

    public boolean execQuietly(DataSource dataSource) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> {
                    exec(connection);
                    return true;
                }
        );
    }

    public <T> boolean execBatchQuietly(
            DataSource dataSource,
            SupplierE<Iterable<T>, SQLException> dataSupplier,
            BiConsumerE<PreparedStatement, T, SQLException> dataMapper
    ) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> {
                    execBatch(
                            connection,
                            dataSupplier,
                            dataMapper
                    );
                    return true;
                }
        );
    }

    public boolean execBlend(DataSource dataSource, Connection connection) {
        try {
            if (connection == null) execQuietly(dataSource);
            else exec(connection);
            return true;
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }
}
