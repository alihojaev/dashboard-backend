package com.parser.core.util.dao.dml;

import com.parser.core.util.dao.Closer;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.FunctionE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Select<E> extends SqlBuilder<Select<E>> implements SqlSelectExecutable<E> {

    FunctionE<ResultSet, Object, SQLException> resultSetToEntity;

    public Select(
            String sql,
            FunctionE<ResultSet, E, SQLException> resultSetToEntity,
            List<BiConsumerE<PreparedStatement, Integer, SQLException>> values
    ) {
        super(sql, values);
        //noinspection unchecked
        this.resultSetToEntity = (FunctionE<ResultSet, Object, SQLException>) resultSetToEntity;
    }

    @Override
    public E exec(Connection connection) throws SQLException {
        Closer closer = new Closer();

        //noinspection TryFinallyCanBeTryWithResources
        try {
            PreparedStatement statement = closer.reg(connection.prepareStatement(sql));
            statementConsumer.accept(statement);
            ResultSet rs = closer.reg(statement.executeQuery());

            //noinspection unchecked
            return (E) resultSetToEntity.apply(rs);
        } finally {
            closer.close();
        }
    }

    @Override
    public <A> A exec(Connection connection, Supplier<A> accumulator, BiConsumerE<A, E, SQLException> transfer) throws SQLException {
        Closer closer = new Closer();

        //noinspection TryFinallyCanBeTryWithResources
        try {
            PreparedStatement statement = closer.reg(connection.prepareStatement(sql));
            statementConsumer.accept(statement);
            ResultSet rs = closer.reg(statement.executeQuery());

            var accum = accumulator.get();

            while (rs.next()) {
                var result = resultSetToEntity.apply(rs);
                //noinspection unchecked
                transfer.accept(accum, (E) result);
            }

            return accum;
        } finally {
            closer.close();
        }
    }
}
