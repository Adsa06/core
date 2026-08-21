package io.github.adsa06.ormsql.conditions;

import java.util.List;

import io.github.adsa06.cbm.CustomBundleManager;

public interface Predicate {
    String toSql(CustomBundleManager bundle); // devuelve algo como "edad > ?"

    List<Object> getParams(); // devuelve [18]

    // LogicalOperator
    static Predicate and(Predicate... predicados) {
        return new CompositePredicate(predicados, LogicalOperator.AND);
    }

    static Predicate or(Predicate... predicados) {
        return new CompositePredicate(predicados, LogicalOperator.OR);
    }

    // Operator
    static Predicate eq(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.EQ, valor);
    }

    static Predicate gt(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.GT, valor);
    }

    static Predicate gteq(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.GTEQ, valor);
    }

    static Predicate lt(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.LT, valor);
    }

    static Predicate lteq(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.LTEQ, valor);
    }

    static Predicate noteq(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.NOTEQ, valor);
    }

    static Predicate like(String columna, Object valor) {
        return new SimplePredicate(columna, Operator.LIKE, valor);
    }

}