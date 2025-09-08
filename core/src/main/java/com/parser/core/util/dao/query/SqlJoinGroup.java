package com.parser.core.util.dao.query;

import java.util.ArrayList;
import java.util.List;

public class SqlJoinGroup<A> {

    boolean included;
    SelectQueryBuilder<A> builder;

    List<JoinConfig> joins;

    SqlJoinGroup(boolean included, SelectQueryBuilder<A> builder) {
        this.included = included;
        this.builder = builder;
        this.joins = new ArrayList<>();
    }

    public GroupSqlJoin<A> innerJoin() {
        return new GroupSqlJoin<>(included, this, JoinType.INNER_JOIN);
    }

    public GroupSqlJoin<A> leftJoin() {
        return new GroupSqlJoin<>(included, this, JoinType.LEFT_JOIN);
    }

    public SelectQueryBuilder<A> endGroup() {
        var builder = this.builder;
        this.builder = null;
        if (included) {
            builder.joins.addAll(joins);
        }
        return builder;
    }
}
