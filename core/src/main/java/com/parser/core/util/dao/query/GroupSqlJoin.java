package com.parser.core.util.dao.query;

public class GroupSqlJoin<A> extends SqlJoin<SqlJoinGroup<A>, A, GroupSqlJoin<A>> {

    SqlJoinGroup<A> sqlJoinGroup;

    GroupSqlJoin(boolean included, SqlJoinGroup<A> sqlJoinGroup, JoinType joinType) {
        super(
                included,
                joinType,
                new ParentHolder<>(
                        sqlJoinGroup,
                        (parent, joinConfig) -> parent.joins.add(joinConfig)
                )
        );
        this.sqlJoinGroup = sqlJoinGroup;
    }

    @Override
    protected void validate() {
        e(sqlJoinGroup == null, "Group not specified");
        super.validate();
    }

    public SelectQueryBuilder<A> endGroup() {
        return noMap().endGroup();
    }
}
