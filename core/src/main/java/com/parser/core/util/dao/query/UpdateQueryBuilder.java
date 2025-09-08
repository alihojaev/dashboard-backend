package com.parser.core.util.dao.query;

import com.parser.core.util.dao.dml.Update;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateQueryBuilder extends InsertUpdateQuery<UpdateQueryBuilder> {

    String whereClause;

    public UpdateQueryBuilder(String table) {
        super(table);
    }

    public UpdateQueryBuilder where(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    @Override
    protected String query() {
        String query = "UPDATE " + table + " SET " +
                columns.stream().map(c -> c + "=?").collect(Collectors.joining(","));

        if (whereClause != null) query += " WHERE " + whereClause;

        return query;
    }

    public Update update() {
        return new Update(query(), values);
    }
}
