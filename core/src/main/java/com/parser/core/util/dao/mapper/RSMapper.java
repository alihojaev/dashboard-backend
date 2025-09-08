package com.parser.core.util.dao.mapper;

import com.parser.core.common.entity.base.BaseEntity;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.functional.ConsumerE;
import com.parser.core.util.functional.FunctionE;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PROTECTED)
@FieldDefaults(level = PROTECTED, makeFinal = true)
public abstract class RSMapper<E, R extends RSMapper<E, R>> {

    ResultSet resultSet;
    String prefix;
    E e;

    private R cast() {
        //noinspection unchecked
        return (R) this;
    }

    public static <E> E map(
            ResultSet resultSet,
            Supplier<E> entitySupplier,
            ConsumerE<RSMapperUpper<E>, SQLException> mapper
    ) throws SQLException {
        var entity = entitySupplier.get();
        mapper.accept(new RSMapperUpper<>(resultSet, null, entity));
        return entity;
    }

    public static <E> E map(
            String prefix,
            ResultSet resultSet,
            Supplier<E> entitySupplier,
            ConsumerE<RSMapperUpper<E>, SQLException> mapper
    ) throws SQLException {
        var entity = entitySupplier.get();
        mapper.accept(new RSMapperUpper<>(resultSet, prefix, entity));
        return entity;
    }

    public static FunctionE<ResultSet, Optional<Long>, SQLException> mapLong(String columnName) {
        return rs -> Optional.ofNullable(rs.next() ? rs.getLong(columnName) : null);
    }

    private String computeColumnName(String name) {
        if (prefix == null) return name;
        else return prefix + name;
    }

    public R map(String column, LongBiConsumer<E> setter) throws SQLException {
        long value = resultSet.getLong(computeColumnName(column));
        setter.accept(e, resultSet.wasNull() ? null : value);
        return cast();
    }

    public R map(String column, BooleanBiConsumer<E> setter) throws SQLException {
        boolean value = resultSet.getBoolean(computeColumnName(column));
        setter.accept(e, resultSet.wasNull() ? null : value);
        return cast();
    }

    public R map(String column, IntegerBiConsumer<E> setter) throws SQLException {
        int value = resultSet.getInt(computeColumnName(column));
        setter.accept(e, resultSet.wasNull() ? null : value);
        return cast();
    }

    public R map(String column, DoubleBiConsumer<E> setter) throws SQLException {
        double value = resultSet.getDouble(computeColumnName(column));
        setter.accept(e, resultSet.wasNull() ? null : value);
        return cast();
    }

    public R map(String column, BigDecimalBiConsumer<E> setter) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(computeColumnName(column));
        setter.accept(e, value);
        return cast();
    }

    public R map(String column, StringBiConsumer<E> setter) throws SQLException {
        String value = resultSet.getString(computeColumnName(column));
        setter.accept(e, value);
        return cast();
    }

    public R map(String column, UUIDBiConsumer<E> setter) throws SQLException {
        UUID value = UUID.fromString(resultSet.getString(computeColumnName(column)));
        setter.accept(e, resultSet.wasNull() ? null : value);
        return cast();
    }

    public <T extends Enum<T>> R map(String column, EnumBiConsumer<E, T> setter, Class<T> type) throws SQLException {
        String value = resultSet.getString(computeColumnName(column));
        setter.accept(e, value == null ? null : Enum.valueOf(type, value));
        return cast();
    }

    public R map(String column, DateBiConsumer<E> setter) throws SQLException {
        java.sql.Date value = resultSet.getDate(computeColumnName(column));
        setter.accept(e, value);
        return cast();
    }

    public R map(String column, TimeBiConsumer<E> setter) throws SQLException {
        java.sql.Time value = resultSet.getTime(computeColumnName(column));
        setter.accept(e, value);
        return cast();
    }

    public R map(String column, TimestampBiConsumer<E> setter) throws SQLException {
        java.sql.Timestamp value = resultSet.getTimestamp(computeColumnName(column));
        setter.accept(e, value);
        return cast();
    }

    public R map(String column, ZonedDateTimeBiConsumer<E> setter) throws SQLException {
        java.time.ZonedDateTime value = ZonedDateTime.ofInstant(resultSet.getTimestamp(computeColumnName(column)).toInstant(), ZoneId.systemDefault());
        setter.accept(e, value);
        return cast();
    }

    public R map(String column, LocalDateTimeBiConsumer<E> setter) throws SQLException {
        LocalDateTime value = LocalDateTime.ofInstant(resultSet.getTimestamp(computeColumnName(column)).toInstant(), ZoneId.systemDefault());
        setter.accept(e, value);
        return cast();
    }

    public R mapIdBase() throws SQLException {
        return map("ID", UUIDBiConsumer.of((e, id) -> ((IdBased) e).setId(id)));
    }

    public R mapBase() throws SQLException {
        var e = ((BaseEntity) this.e);

        return
                map(
                        "CREATED_BY",
                        UUIDBiConsumer.of((ignored, createdById) -> e.setCreatedBy(createdById))
                )
                        .map(
                                "MODIFIED_BY",
                                UUIDBiConsumer.of((ignored, modifiedById) -> e.setModifiedBy(modifiedById))
                        )
                        .map(
                                "CDT",
                                LocalDateTimeBiConsumer.of((ignored, cdt) -> e.setCdt(cdt))
                        )
                        .map(
                                "MDT",
                                LocalDateTimeBiConsumer.of((ignored, mdt) -> e.setMdt(mdt))
                        )
                        .map(
                                "RDT",
                                LocalDateTimeBiConsumer.of((ignored, rdt) -> e.setRdt(rdt))
                        );
    }

    public R consume(ConsumerE<E, SQLException> consumer) throws SQLException {
        consumer.accept(e);
        return cast();
    }
}
