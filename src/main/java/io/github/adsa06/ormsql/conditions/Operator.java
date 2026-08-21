package io.github.adsa06.ormsql.conditions;

public enum Operator {
    EQ("equal"),
    GT("greaterThan"),
    GTEQ("greaterThanOrEqualTo"),
    LT("lessThan"),
    LTEQ("lessThanOrEqualTo"),
    NOTEQ("notEqualTo"),
    LIKE("like");

    private String statmentId;

    Operator(String statmentId) {
        this.statmentId = statmentId;
    }

    public String getStatmentId() {
        return statmentId;
    }
}
