package com.parser.core.util.dao.query;

import com.parser.core.util.dao.ParametrizedSql;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
abstract class TableQuery<T extends TableQuery<?>> extends ParametrizedSql<T> {

    String table;

    protected TableQuery(String table) {
        super(null);
        this.table = table;
    }
}
