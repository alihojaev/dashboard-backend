package com.parser.core.util.dao.query;

import com.parser.core.util.dao.ParametrizedSql;
import com.parser.core.util.dao.dml.Select;
import com.parser.core.util.dao.dml.SqlSelectExecutable;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.FunctionE;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class SelectQueryBuilder<T> extends ParametrizedSql<SelectQueryBuilder<T>> implements SqlSelectExecutable<T> {

    FunctionE<ResultSet, T, SQLException> mapper;
    List<JoinConfig> joins;
    String table;
    String tableAlias;
    Set<String> columns;
    String completeColumns;
    String whereClause;
    String orderByClause;
    Integer offset;
    Integer limit;

    public SelectQueryBuilder(FunctionE<ResultSet, T, SQLException> mapper) {
        super(null);
        this.mapper = mapper;
        this.joins = new ArrayList<>();
        completeColumns("*");
    }

    static void e(boolean thr, String message) {
        if (thr) throw new IllegalStateException(message);
    }

    private void validate() {
        e(mapper == null, "Mapper is null");
        e(table == null || table.isBlank(), "Table name not specified");
        e(tableAlias != null && tableAlias.isBlank(), "Table alias can not be empty");
        e((columns == null || columns.isEmpty()) && completeColumns == null, "Columns not specified");
        e(columns != null && columns.stream().anyMatch(col -> col == null || col.isBlank()), "Some of columns are empty");
        e(whereClause != null && whereClause.isBlank(), "Where clause can not be empty");
        e(orderByClause != null && orderByClause.isBlank(), "Order by clause can not be empty");
        e(offset != null && offset < 1, "Offset can not be less then 1");
        e(limit != null && limit < 1, "Limit can not be less then 1");
    }

    public SelectQueryBuilder<T> table(String name) {
        this.table = name;
        return this;
    }

    public SelectQueryBuilder<T> table(String name, String alias) {
        this.table = name;
        this.tableAlias = alias;
        return this;
    }

    public SelectQueryBuilder<T> columns(Collection<String> columns) {
        this.columns = new HashSet<>(columns);
        return this;
    }

    public SelectQueryBuilder<T> columns(String... columns) {
        return columns(Set.of(columns));
    }

    public SelectQueryBuilder<T> completeColumns(String columns) {
        this.completeColumns = columns;
        return this;
    }

    public SelectQueryBuilder<T> where(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    public SelectQueryBuilder<T> orderBy(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    public SelectQueryBuilder<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public SelectQueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SelectJoin<T> innerJoin(BooleanSupplier condition) {
        return new SelectJoin<>(condition.getAsBoolean(), this, JoinType.INNER_JOIN);
    }

    public SelectJoin<T> innerJoin() {
        return new SelectJoin<>(true, this, JoinType.INNER_JOIN);
    }

    public SelectJoin<T> leftJoin(BooleanSupplier condition) {
        return new SelectJoin<>(condition.getAsBoolean(), this, JoinType.LEFT_JOIN);
    }

    public SelectJoin<T> leftJoin() {
        return new SelectJoin<>(true, this, JoinType.LEFT_JOIN);
    }

    public SqlJoinGroup<T> joinGroup(BooleanSupplier condition) {
        return new SqlJoinGroup<>(condition.getAsBoolean(), this);
    }

    protected String query() {
        var valuesJoiner = new StringJoiner(",");
        var joinsBuilder = new StringBuilder();
        var whereBuilder = new StringBuilder();

        var mainTableAlias = tableAlias == null ? table : tableAlias;

        if (columns == null) {
            valuesJoiner.add(Objects.requireNonNull(completeColumns));
        } else {
            columns.stream()
                    .map(col -> mainTableAlias + '.' + col)
                    .forEach(valuesJoiner::add);
        }

        if (whereClause != null) {
            whereBuilder.append(whereClause);
        }

        joins.forEach(
                join -> {

                    var aliasPrefix = join.joinTableAlias == null ? join.joinTable : join.joinTableAlias;

                    if (join.columns != null) {
                        join.columns.stream()
                                .map(col -> aliasPrefix + '.' + col + ' ' + aliasPrefix + '_' + col)
                                .forEach(valuesJoiner::add);
                    } else {
                        valuesJoiner.add(join.completeColumns);
                    }

                    if (join.joinType == JoinType.LEFT_JOIN) {
                        valuesJoiner.add("CASE WHEN (" + join.joinExpression + ") THEN 1 ELSE 0 END " + aliasPrefix + "_JOINED");
                    }

                    joinsBuilder
                            .append(join.joinType.getValue())
                            .append(' ')
                            .append(join.joinTable);

                    if (join.joinTableAlias != null) {
                        joinsBuilder.append(' ').append(join.joinTableAlias);
                    }

                    joinsBuilder.append(" ON ")
                            .append(join.joinExpression)
                            .append('\n');

                    if (join.whereClause != null) {
                        if (whereBuilder.length() > 0) {
                            whereBuilder
                                    .append(' ')
                                    .append(join.whereOperator)
                                    .append(' ');
                        }

                        whereBuilder.append(join.whereClause);
                    }
                }
        );

        var selectBuilder = new StringBuilder();

        selectBuilder.append("SELECT\n")
                .append(valuesJoiner)
                .append("\nFROM ")
                .append(table);

        if (tableAlias != null) {
            selectBuilder.append(' ').append(tableAlias);
        }

        selectBuilder
                .append('\n')
                .append(joinsBuilder);

        if (whereBuilder.length() > 0) {
            selectBuilder
                    .append("WHERE ")
                    .append(whereBuilder)
                    .append('\n');
        }

        if (orderByClause != null) {
            selectBuilder
                    .append("ORDER BY ")
                    .append(orderByClause);
        }

        if (offset != null && limit != null) {
            selectBuilder.append(" LIMIT ").append(limit);
            selectBuilder.append(" OFFSET ").append(offset * limit - limit);
        }

        return selectBuilder.toString();
    }

    public Select<T> select() {
        validate();
        var mapper = this.mapper;
        if (joins.size() > 0) {
            mapper = resultSet -> {
                var primaryResult = this.mapper.apply(resultSet);
                if (primaryResult == null)
                    return null;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < joins.size(); i++) {
                    var join = joins.get(i);
                    if (join.combiner != null) {
                        if (join.joinType == JoinType.LEFT_JOIN) {
                            var aliasPrefix = join.joinTableAlias == null ? join.joinTable : join.joinTableAlias;
                            if (!resultSet.getBoolean(aliasPrefix + "_JOINED")) {
                                continue;
                            }
                        }
                        join.combiner.accept(resultSet, primaryResult);
                    }
                }
                return primaryResult;
            };
        }

        joins.forEach(join -> values.addAll(join.values));

        return new Select<>(query(), mapper, values);
    }

    @Override
    public T exec(Connection connection) throws SQLException {
        return select().exec(connection);
    }

    @Override
    public <A> A exec(
            Connection connection,
            Supplier<A> accumulator,
            BiConsumerE<A, T, SQLException> transfer
    ) throws SQLException {
        return select().exec(connection, accumulator, transfer);
    }
}
