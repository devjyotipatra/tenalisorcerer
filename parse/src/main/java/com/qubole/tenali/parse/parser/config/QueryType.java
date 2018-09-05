package com.qubole.tenali.parse.parser.config;

public final class QueryType extends TenaliType<QueryType.Type>  {

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

    public QueryType(Type value) throws IllegalArgumentException {
        super(value);
    }

    public Type getType() {
        return type;
    }

    @Override public boolean equals(Object other) {
        QueryType o = (QueryType) other;
        return o.type == this.type;
    }

    public String toString() {
        return type.toString();
    }
}
