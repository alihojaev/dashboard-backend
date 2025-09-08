package com.parser.core.util.dao;

import com.parser.core.util.dao.dml.Delete;
import com.parser.core.util.dao.dml.Insert;
import com.parser.core.util.dao.dml.Select;
import com.parser.core.util.dao.dml.Update;
import com.parser.core.util.dao.query.DeleteQueryBuilder;
import com.parser.core.util.dao.query.InsertQueryBuilder;
import com.parser.core.util.dao.query.SelectQueryBuilder;
import com.parser.core.util.dao.query.UpdateQueryBuilder;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.ConsumerE;
import com.parser.core.util.functional.FunctionE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SqlBuilder<T extends SqlBuilder<?>> extends ParametrizedSql<T> {

    String sql;
    ConsumerE<PreparedStatement, SQLException> statementConsumer;

    protected SqlBuilder(String sql, List<BiConsumerE<PreparedStatement, Integer, SQLException>> values) {
        super(values);
        this.sql = sql;
        this.statementConsumer =
                statement -> {
                    for (int i = 0; i < this.values.size(); i++) {
                        this.values.get(i).accept(statement, i + 1);
                    }
                };
    }

    public static Insert insert(@Language("sql") String sql) {
        return new Insert(sql, null);
    }

    public static InsertQueryBuilder queryInsert(@Language(value = "sql", prefix = "INSERT INTO ", suffix = " ()") String table) {
        return new InsertQueryBuilder(table);
    }

    public static Update update(@Language("sql") String sql) {
        return new Update(sql, null);
    }

    public static UpdateQueryBuilder queryUpdate(@Language(value = "sql", prefix = "UPDATE ", suffix = " SET") String table) {
        return new UpdateQueryBuilder(table);
    }

    public static <E> Select<E> select(
            @Language("sql") String sql,
            FunctionE<ResultSet, E, SQLException> mapper
    ) {
        return new Select<>(sql, mapper, null);
    }

    public static <T> SelectQueryBuilder<T> querySelect(FunctionE<ResultSet, T, SQLException> mapper) {
        return new SelectQueryBuilder<>(mapper);
    }

    public static Delete delete(@Language("sql") String sql) {
        return new Delete(sql, null);
    }

    public static DeleteQueryBuilder queryDelete(@Language(value = "sql", prefix = "DELETE FROM ") String table) {
        return new DeleteQueryBuilder(table);
    }

    public T debugSql() {
        log.debug('\n' + sql);
        return casted();
    }

    public T debugSql(Consumer<String> sqlConsumer) {
        sqlConsumer.accept(sql);
        return casted();
    }

    public T statement(ConsumerE<PreparedStatement, SQLException> statementConsumer) {
        this.statementConsumer = statementConsumer;
        return casted();
    }
}
