package io.github.adsa06.ormsql.query.predicate;

import java.util.List;

import io.github.adsa06.cbm.CustomBundleManager;

public class SimplePredicate implements Predicate {
    private String columna;
    private Operator operator;
    private Object valor;

    public SimplePredicate(String columna, Operator operator, Object valor) {
        this.columna = columna;
        this.operator = operator;
        this.valor = valor;
    }

    // Nombres de columna sin proteger (quoting)
    @Override
    public String toSql(CustomBundleManager bundle) {
        return columna + " " + bundle.getString(operator.getStatmentId()) + " ?";
    }

    @Override
    public List<Object> getParams() {
        return List.of(valor);
    }
}
