package com.parser.core.util.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CustomRsqlParser {

    private static final Set<ComparisonOperator> CUSTOM_OPERATORS = new HashSet<>();

    static {
        CUSTOM_OPERATORS.add(CustomRSQLOperators.LIKE);
        CUSTOM_OPERATORS.add(CustomRSQLOperators.ILIKE);
        CUSTOM_OPERATORS.add(CustomRSQLOperators.IS_NULL);
        CUSTOM_OPERATORS.add(CustomRSQLOperators.NOT_NULL);
        CUSTOM_OPERATORS.add(CustomRSQLOperators.MEMBER);
        CUSTOM_OPERATORS.addAll(RSQLOperators.defaultOperators());
    }

    private final RSQLParser parser;

    public CustomRsqlParser() {
        this.parser = new RSQLParser(CUSTOM_OPERATORS);
    }

    public Node parse(String query) {
        return parser.parse(query);
    }
}
