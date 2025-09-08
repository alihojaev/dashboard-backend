package com.parser.core.util.dao;

import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.functional.BiConsumerE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class ParametrizedSql<T extends ParametrizedSql<?>> {

    List<BiConsumerE<PreparedStatement, Integer, SQLException>> values;

    protected ParametrizedSql(List<BiConsumerE<PreparedStatement, Integer, SQLException>> values) {
        this.values = values == null ? new ArrayList<>() : values;
    }

    protected T value(BiConsumerE<PreparedStatement, Integer, SQLException> statementConsumer) {
        values.add(statementConsumer);
        return casted();
    }

    private static <V> boolean iterate(Class<V> clazz, Object first, Iterator<V> iterator, Consumer<V> consumer) {
        if (clazz.isAssignableFrom(first.getClass())) {
            //noinspection unchecked
            consumer.accept((V) first);
            while (iterator.hasNext()) consumer.accept(iterator.next());
            return true;
        } else {
            return false;
        }
    }

    @SafeVarargs
    public final <V> T values(V... values) {
        return values(Arrays.asList(values));
    }

    public T values(Iterable<?> values) {
        //noinspection rawtypes
        Iterator iterator = values.iterator();
        if (iterator.hasNext()) {
            var next = iterator.next();
            // noinspection unchecked
            if (
                    !(
                            iterate(Enum.class, next, iterator, this::value) ||
                                    iterate(String.class, next, iterator, this::value) ||
                                    iterate(Long.class, next, iterator, this::value) ||
                                    iterate(Timestamp.class, next, iterator, this::value) ||
                                    iterate(BigDecimal.class, next, iterator, this::value) ||
                                    iterate(IdBased.class, next, iterator, this::value) ||
                                    iterate(Boolean.class, next, iterator, this::value) ||
                                    iterate(Double.class, next, iterator, this::value) ||
                                    iterate(Date.class, next, iterator, this::value) ||
                                    iterate(Time.class, next, iterator, this::value)
                    )
            ) {
                throw new RuntimeException("Unsupported type: " + next.getClass().getCanonicalName());
            }
        }
        return casted();
    }

    public T value(Long value) {
        return value == null ? nullValue() :
                value((statement, index) -> statement.setLong(index, value));
    }

    public T value(Boolean value) {
        return value == null ? nullValue() :
                value((statement, index) -> statement.setBoolean(index, value));
    }

    public T value(Integer value) {
        return value == null ? nullValue() :
                value((statement, index) -> statement.setInt(index, value));
    }

    public T value(Double value) {
        return value == null ? nullValue() :
                value((statement, index) -> statement.setDouble(index, value));
    }

    public T value(String value) {
        return value == null ? nullValue() :
                value((statement, index) -> statement.setString(index, value));
    }

    public T value(UUID value) {
        return value == null ? nullValue() :
                value((statement, index) -> statement.setObject(index, value));
    }

    public T value(Timestamp value) {
        return value((statement, index) -> statement.setTimestamp(index, value));
    }

    public T value(Date value) {
        return value((statement, index) -> statement.setDate(index, value));
    }

    public T value(Time value) {
        return value((statement, index) -> statement.setTime(index, value));
    }

    public T value(BigDecimal value) {
        return value((statement, index) -> statement.setBigDecimal(index, value));
    }

    public T value(IdBased value) {
        return value == null || value.getId() == null ? nullValue() :
                value(value.getId());
    }

    public T value(@SuppressWarnings("rawtypes") Enum value) {
        return value == null ? nullValue() :
                value(value.name());
    }

    public T nullValue() {
        return value((statement, index) -> statement.setNull(index, Types.NULL));
    }

    public T repeat() {
        if (values.isEmpty()) {
            throw new IllegalStateException("No params to repeat");
        } else {
            values.add(values.get(values.size() - 1));
        }
        return casted();
    }

    public T repeat(int nTimes) {
        if (nTimes < 1) throw new IllegalStateException("Repeat number can not be less then 1");

        if (values.isEmpty()) {
            throw new IllegalStateException("No params to repeat");
        } else {
            var statementConsumer = values.get(values.size() - 1);
            for (int i = 0; i < nTimes; i++) values.add(statementConsumer);
        }
        return casted();
    }

    public T repeat(int nTimes, int nLast) {
        if (nTimes < 1) throw new IllegalStateException("Repeat number can not be less then 1");
        if (nLast < 1) throw new IllegalStateException("Repeat last number can not be less then 1");

        if (values.isEmpty()) {
            throw new IllegalStateException("No params to repeat");
        } else {
            if (nLast == 1) {
                var statementConsumer = values.get(values.size() - 1);
                for (int i = 0; i < nTimes; i++) values.add(statementConsumer);
            } else {
                if (values.size() < nLast)
                    throw new IllegalStateException("Repeat last number can not be more then params count");
                var statementConsumerList = values.subList(values.size() - nLast - 1, values.size() - 1);
                for (int i = 0; i < nTimes; i++) values.addAll(statementConsumerList);
            }
        }
        return casted();
    }

    public T limit(int limit) {
        return casted();
    }

    protected T casted() {
        //noinspection unchecked
        return (T) this;
    }
}
