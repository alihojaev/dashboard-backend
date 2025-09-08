package com.parser.core.util.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.stereotype.Component;

@Component
public class CustomRSQLOperators extends RSQLOperators {

    public static final ComparisonOperator LIKE = new ComparisonOperator("=like=");
    public static final ComparisonOperator ILIKE = new ComparisonOperator("=ilike=");
    public static final ComparisonOperator IS_NULL = new ComparisonOperator("=isnull=");
    public static final ComparisonOperator NOT_NULL = new ComparisonOperator("=notnull=");
    public static final ComparisonOperator MEMBER = new ComparisonOperator("=member=");

}
