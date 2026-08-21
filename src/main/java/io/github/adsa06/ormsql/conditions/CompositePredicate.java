package io.github.adsa06.ormsql.conditions;

import java.util.List;
import java.util.stream.Collectors;

import io.github.adsa06.cbm.CustomBundleManager;

public class CompositePredicate implements Predicate {
    private List<Predicate> predicates;
    private LogicalOperator op; // AND, OR
    // toSql() -> "(" + join con " AND "/" OR " + ")"

    public CompositePredicate(Predicate[] predicados, LogicalOperator op) {
        this.predicates = List.of(predicados);
        this.op = op;
    }

    @Override
    public String toSql(CustomBundleManager bundle) {
        return "(" + predicates.stream().map(p -> p.toSql(bundle))
                .collect(Collectors.joining(" " + bundle.getString(op.getStatmentId()) + " ")) + ")";
    }

    @Override
    public List<Object> getParams() {
        return predicates.stream()
                .flatMap(p -> p.getParams().stream())
                .collect(Collectors.toList());
    }
}