package com.parser.core.util.dao.query;

import com.parser.core.util.dao.dml.Delete;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteQueryBuilder extends TableQuery<DeleteQueryBuilder> {
    String whereClause;

    public DeleteQueryBuilder(String table) {
        super(table);
    }

    public DeleteQueryBuilder where(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    private String query() {
        String query = "DELETE FROM " + table;
        if (whereClause != null) query += " WHERE " + whereClause;

        return query;
    }

    public Delete delete() {
        return new Delete(query(), values);
    }
}
