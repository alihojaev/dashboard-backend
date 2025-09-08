package com.parser.core.util.dao.dml;

import com.parser.core.util.dao.Closer;
import com.parser.core.util.dao.DaoException;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.dao.TransactionUtil;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.ConsumerE;
import com.parser.core.util.functional.SupplierE;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Delete extends SqlBuilder<Delete> {

    public Delete(String sql, List<BiConsumerE<PreparedStatement, Integer, SQLException>> values) {
        super(sql, values);
    }

    public Delete statement(ConsumerE<PreparedStatement, SQLException> statementConsumer) {
        this.statementConsumer = statementConsumer;
        return this;
    }

    public int exec(Connection connection) throws SQLException {
        Closer closer = new Closer();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            PreparedStatement statement = closer.reg(connection.prepareStatement(sql));

            statementConsumer.accept(statement);

            return statement.executeUpdate();
        } finally {
            closer.close();
        }
    }

    public void execRequireAffected(Connection connection) throws SQLException {
        var affected = exec(connection);
        if (affected == 0) throw new SQLException("No rows affected");
    }

    public <T> void execBatch(
            Connection connection,
            SupplierE<Iterable<T>, SQLException> dataSupplier,
            BiConsumerE<PreparedStatement, T, SQLException> dataMapper,
            BiConsumerE<T, Integer, SQLException> processAffectedConsumer
    ) throws SQLException {
        for (T entity : dataSupplier.get()) {
            this.statementConsumer = statement -> dataMapper.accept(statement, entity);
            int affected = exec(connection);
            if (processAffectedConsumer != null) processAffectedConsumer.accept(entity, affected);
        }
    }

    public int execQuietly(DataSource dataSource) {
        return TransactionUtil.transaction(
                dataSource,
                this::exec
        );
    }

    public <T> void execBatchQuietly(
            DataSource dataSource,
            SupplierE<Iterable<T>, SQLException> dataSupplier,
            BiConsumerE<PreparedStatement, T, SQLException> dataMapper,
            BiConsumerE<T, Integer, SQLException> processAffectedConsumer
    ) {
        TransactionUtil.transactionNoRet(
                dataSource,
                connection -> execBatch(
                        connection,
                        dataSupplier,
                        dataMapper,
                        processAffectedConsumer
                )
        );
    }

    public int execBlend(DataSource dataSource, Connection connection) {
        try {
            if (connection == null) return execQuietly(dataSource);
            else return exec(connection);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }
}
