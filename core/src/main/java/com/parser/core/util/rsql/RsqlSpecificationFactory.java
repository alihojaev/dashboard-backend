package com.parser.core.util.rsql;

import cz.jirutka.rsql.parser.ast.*;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RsqlSpecificationFactory<T> {

    @Bean
    public Specification<T> createSpecification(String search) {
//rdt=isnull=true
        if (search == null || search.isEmpty()) search = "rdt=isnull=true";
        else search += ";rdt=isnull=true";
        CustomRsqlParser parser = new CustomRsqlParser();
        Node rootNode = parser.parse(search);
        RSQLVisitor<Specification<T>, Void> visitor = new CustomRsqlVisitor<>();
        return rootNode.accept(visitor, null);
    }

    @Bean
    public Specification<T> createSpecification(Node node) {
        if (node instanceof LogicalNode) {
            return createSpecification((LogicalNode) node);
        }
        if (node instanceof ComparisonNode) {
            return createSpecification((ComparisonNode) node);
        }
        return null;
    }

    @Bean
    public Specification<T> createSpecification(LogicalNode logicalNode) {
        List<Specification<T>> specs = logicalNode.getChildren().stream()
                .map(this::createSpecification)
                .toList();

        Specification<T> result = specs.get(0);
        if (logicalNode instanceof AndNode) {
            for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).and(specs.get(i));
            }
        } else if (logicalNode instanceof OrNode) {
            for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).or(specs.get(i));
            }
        }
        return result;
    }

    @Bean
    public Specification<T> createSpecification(ComparisonNode comparisonNode) {
        ComparisonOperator operator = comparisonNode.getOperator();
        String selector = comparisonNode.getSelector();
        List<String> arguments = comparisonNode.getArguments();

        switch (operator.getSymbol()) {
            case "=isnull=":
                return (root, query, cb) -> cb.isNull(root.get(selector));
            case "=notnull=":
                return (root, query, cb) -> cb.isNotNull(root.get(selector));
            default:
                // Add other operators handling if necessary
                return null;
        }
    }
}
