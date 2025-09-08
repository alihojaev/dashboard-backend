package com.parser.core.util.dao.query;

import com.parser.core.util.dao.ParametrizedSql;
import com.parser.core.util.dao.mapper.*;
import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.ConsumerE;
import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class SqlJoin<P, A, J extends SqlJoin<P, A, ?>> extends ParametrizedSql<J> {
    boolean included;
    JoinConfig joinConfig;
    ParentHolder<P> parentHolder;

    SqlJoin(
            boolean included,
            JoinType joinType,
            ParentHolder<P> parentHolder
    ) {
        super(null);
        this.included = included;
        joinConfig = new JoinConfig(joinType, super.values);
        this.parentHolder = parentHolder;
    }

    protected static void e(boolean thr, String message) {
        if (thr) throw new IllegalStateException(message);
    }

    protected void validate() {
        joinConfig.validate();
    }

    List<BiConsumerE<PreparedStatement, Integer, SQLException>> getValues() {
        return joinConfig.values;
    }

    @Override
    protected J value(BiConsumerE<PreparedStatement, Integer, SQLException> statementConsumer) {
        return included ? super.value(statementConsumer) : casted();
    }

    @Override
    public J repeat() {
        if (included) return super.repeat();
        return casted();
    }

    @Override
    public J repeat(int nTimes) {
        if (included) return super.repeat(nTimes);
        return casted();
    }

    @Override
    public J repeat(int nTimes, int nLast) {
        if (included) return super.repeat(nTimes, nLast);
        return casted();
    }

    public J withTable(String table) {
        joinConfig.joinTable = table;
        return casted();
    }

    public J withTable(String table, String alias) {
        joinConfig.joinTableAlias = alias;
        return withTable(table);
    }

    public J on(String expression) {
        joinConfig.joinExpression = expression;
        return casted();
    }

    public J columns(@NonNull String... columns) {
        joinConfig.columns = Set.of(columns);
        return casted();
    }

    public J completeColumns(String columns) {
        joinConfig.completeColumns = columns;
        return casted();
    }

    public J columns(@NonNull Collection<String> columns) {
        joinConfig.columns = new HashSet<>(columns);
        return casted();
    }

    public J andWhere(String condition) {
        joinConfig.whereOperator = "AND";
        joinConfig.whereClause = condition;
        return casted();
    }

    public J orWhere(String condition) {
        joinConfig.whereOperator = "OR";
        joinConfig.whereClause = condition;
        return casted();
    }

    private P pMap(Consumer<String> ifIncluded) {
        validate();
        var parentHolder = this.parentHolder;
        this.parentHolder = null;
        if (included) {
            var prefix = joinConfig.joinTableAlias == null ? joinConfig.joinTable : joinConfig.joinTableAlias;
            ifIncluded.accept(prefix);
            parentHolder.addConfig(joinConfig);
        }
        return parentHolder.parent;
    }

    public P noMap() {
        return pMap(ignored -> joinConfig.combiner = null);
    }

    public <T> P map(
            @NonNull Supplier<T> modelSuppler,
            @NonNull ConsumerE<RSMapperUpper<T>, SQLException> mapper,
            @NonNull BiConsumer<A, T> combiner
    ) {
        return pMap(prefix -> joinConfig.combiner = (resultSet, accumulator) -> {
            //noinspection unchecked
            combiner.accept(
                    (A) accumulator,
                    RSMapper.map(
                            prefix + "_",
                            resultSet,
                            modelSuppler,
                            mapper
                    )
            );
        });
    }

    public P map(@NonNull ConsumerE<RSMapperUpper<A>, SQLException> mapper) {
        return pMap(prefix -> joinConfig.combiner = (resultSet, accumulator) -> {
            //noinspection unchecked
            RSMapper.map(
                    prefix + "_",
                    resultSet,
                    () -> (A) accumulator,
                    mapper
            );
        });
    }

    public P map(String column, LongBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, StringBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, BooleanBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, IntegerBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, DoubleBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, BigDecimalBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public <T extends Enum<T>> P map(String column, EnumBiConsumer<A, T> setter, Class<T> type) {
        return map(rsMapper -> rsMapper.map(column, setter, type));
    }

    public P map(String column, DateBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, TimeBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, TimestampBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }

    public P map(String column, ZonedDateTimeBiConsumer<A> setter) {
        return map(rsMapper -> rsMapper.map(column, setter));
    }
}
