package com.parser.core.util.dao.query;

import com.parser.core.util.dao.dml.Insert;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.StringJoiner;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertQueryBuilder extends InsertUpdateQuery<InsertQueryBuilder> {

    @NonFinal
    String primaryKeyName;
    @NonFinal
    String primaryKeySequence;

    public InsertQueryBuilder(String table) {
        super(table);
    }

    public InsertQueryBuilder primaryKey(String columnName, String sequence) {
        primaryKeyName = columnName;
        primaryKeySequence = sequence;
        return this;
    }

    public InsertQueryBuilder primaryKey(String sequence) {
        return primaryKey("id", sequence);
    }


    @Override
    protected String query() {

        StringJoiner names = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");

        if (primaryKeyName != null && primaryKeySequence != null) {
            names.add(primaryKeyName);
            values.add("'" + UUID.randomUUID().toString() + "'");
        }

        columns.forEach(column -> {
            names.add(column);
            values.add("?");
        });

        return "INSERT INTO " + table + "(" + names + ") VALUES (" + values + ")";
    }

    public Insert insert() {
        return new Insert(query(), values);
    }

    public Insert insert(String addQuery) {
        return new Insert(query() + addQuery, values);
    }
}
