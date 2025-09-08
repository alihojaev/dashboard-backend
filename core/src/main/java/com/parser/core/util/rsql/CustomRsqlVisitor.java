package com.parser.core.util.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CustomRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return node.getChildren()
                .stream()
                .map(n -> n.accept(this, param))
                .reduce(Specification::and)
                .orElse(null);
    }

    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return node.getChildren()
                .stream()
                .map(n -> n.accept(this, param))
                .reduce(Specification::or)
                .orElse(null);
    }

    @Override
    public Specification<T> visit(ComparisonNode node, Void param) {
        return new CustomRsqlSpecification<>(node.getSelector(), node.getOperator(), node.getArguments());
    }
}