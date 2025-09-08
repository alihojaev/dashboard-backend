package com.parser.core.util.dao.query;

public class SelectJoin<A> extends SqlJoin<SelectQueryBuilder<A>, A, SelectJoin<A>> {

    SelectQueryBuilder<A> builder;

    SelectJoin(boolean included, SelectQueryBuilder<A> builder, JoinType joinType) {
        super(
                included,
                joinType,
                new ParentHolder<>(builder, (b, config) -> b.joins.add(config))
        );
        this.builder = builder;
    }

    @Override
    protected void validate() {
        e(builder == null, "Builder not specified");
        super.validate();
    }
}
