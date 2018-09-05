package com.qubole.tenali.parse.parser.config;

public class Query {

    Type type;

    public enum Type {
        SET,
        ADD_JAR,
        USE,
        CREATE_FUNCTION,
        INSERT_INTO,
        INSERT_OVERWRITE,
        SELECT,
        DROP_TABLE,
        DROP_VIEW,
        ALTER_TABLE,
        CREATE_TABLE,
        CTAS,
        CTE,
        CREATE_VIEW,
        UNKNOWN;
    }

    public Query(Type value) throws IllegalArgumentException {
        this.type = value;
    }

    public Type getType() {
        return type;
    }

    @Override public boolean equals(Object other) {
        Query o = (Query) other;
        return o.type == this.type;
    }

    public String toString() {
        return type.toString();
    }
}
