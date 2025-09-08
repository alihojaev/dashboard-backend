package com.parser.core.util.dao.query;

import com.parser.core.common.entity.base.BaseEntity;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.functional.BiConsumerE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
abstract class InsertUpdateQuery<T extends InsertUpdateQuery<?>> extends TableQuery<T> {

    List<String> columns;

    InsertUpdateQuery(String table) {
        super(table);
        columns = new ArrayList<>();
    }

    protected abstract String query();

    private T column(String name, BiConsumerE<PreparedStatement, Integer, SQLException> statementConsumer) {
        //noinspection unchecked
        return (T) column(name)
                .value(statementConsumer);
    }

    public T columns(String... names) {
        columns.addAll(List.of(names));
        return casted();
    }

    public T column(String name) {
        columns.add(name);
        return casted();
    }

    public T column(String name, Long value) {
        column(name);
        return value == null ? nullValue() :
                value((statement, index) -> statement.setLong(index, value));
    }

    public T column(String name, Boolean value) {
        column(name);
        return value == null ? nullValue() :
                value((statement, index) -> statement.setBoolean(index, value));
    }

    public T column(String name, Integer value) {
        column(name);
        return value == null ? nullValue() :
                value((statement, index) -> statement.setInt(index, value));
    }

    public T column(String name, Double value) {
        column(name);
        return value == null ? nullValue() :
                value((statement, index) -> statement.setDouble(index, value));
    }

    public T column(String name, String value) {
        //noinspection unchecked
        return (T) column(name)
                .value((statement, index) -> statement.setString(index, value));
    }

    public T column(String name, UUID value) {
        //noinspection unchecked
        return (T) column(name)
                .value((statement, index) -> statement.setObject(index, value));
    }

    public T column(String name, Timestamp value) {
        //noinspection unchecked
        return (T) column(name)
                .value((statement, index) -> statement.setTimestamp(index, value));
    }

    public T column(String name, Date value) {
        //noinspection unchecked
        return (T) column(name)
                .value((statement, index) -> statement.setDate(index, value));
    }

    public T column(String name, Time value) {
        //noinspection unchecked
        return (T) column(name)
                .value((statement, index) -> statement.setTime(index, value));
    }

    public T column(String name, BigDecimal value) {
        //noinspection unchecked
        return (T) column(name)
                .value((statement, index) -> statement.setBigDecimal(index, value));
    }

    public T column(String name, IdBased value) {
        return value == null || value.getId() == null ? nullColumn(name) : this.column(
                name,
                (statement, index) -> statement.setObject(index, value.getId().toString())
        );
    }

    public T column(String name, @SuppressWarnings("rawtypes") Enum value) {
        column(name);
        return value == null ? nullColumn(name) :
                value((statement, index) -> statement.setString(index, value.name()));
    }

    public T baseColumns(BaseEntity baseEntity) {
        //noinspection unchecked
        return (T) column("CREATED_BY_ID", baseEntity.getCreatedBy())
                .column("MODIFIED_BY_ID", baseEntity.getModifiedBy() == null ? null : baseEntity.getModifiedBy())
                .column("CDT", baseEntity.getCdt().getNano())
                .column("MDT", baseEntity.getMdt().getNano())
                .column("RDT", baseEntity.getRdt().getNano());
    }

    public T nullColumn(String name) {
        //noinspection unchecked
        return (T) column(name).nullValue();
    }
}
