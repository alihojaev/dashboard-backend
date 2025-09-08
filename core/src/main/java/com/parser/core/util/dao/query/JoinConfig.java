package com.parser.core.util.dao.query;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

class JoinConfig {
    final JoinType joinType;
    final List<BiConsumerE<PreparedStatement, Integer, SQLException>> values;

    String joinTable;
    String joinTableAlias;

    Set<String> columns;
    String completeColumns;

    String joinExpression;

    String whereOperator;
    String whereClause;

    BiConsumerE<ResultSet, Object, SQLException> combiner;

    JoinConfig(
            JoinType joinType,
            List<BiConsumerE<PreparedStatement, Integer, SQLException>> values
    ) {
        this.joinType = joinType;
        this.values = values;
    }

    static void e(boolean thr, String message) {
        if (thr) throw new IllegalStateException(message);
    }

    void validate() {
        e(joinTable == null || joinTable.isBlank(), "Join table not specified");
        e(joinTableAlias != null && joinTableAlias.isBlank(), "Join table alias can not be empty");
        e(columns != null && columns.isEmpty(), "Column(s) not specified or empty");
        e(completeColumns != null && completeColumns.isEmpty(), "Column(s) not specified or empty");
        e(columns != null && columns.stream().anyMatch(col -> col == null || col.isBlank()), "Some of columns are empty");
        e(joinExpression == null || joinExpression.isBlank(), "Join clause not specified");
        e(whereClause != null && whereClause.isBlank(), "Where clause can not be empty");
    }
}
