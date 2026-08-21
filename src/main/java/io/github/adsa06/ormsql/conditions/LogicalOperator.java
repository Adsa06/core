package io.github.adsa06.ormsql.conditions;

public enum LogicalOperator {
    AND("and"),
    OR("or");

    private String statmentId;

    LogicalOperator(String statmentId) {
        this.statmentId = statmentId;
    }

    public String getStatmentId() {
        return statmentId;
    }
}
