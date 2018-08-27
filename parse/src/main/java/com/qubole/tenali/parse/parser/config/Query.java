package com.qubole.tenali.parse.parser.config;

public class Query {

    Type type;

    public enum Type {
        SET("set"),
        ADD_JAR("add_jar"),
        USE("use"),
        CREATE_FUNCTION("create_function"),
        INSERT_INTO("insert_into"),
        INSERT_OVERWRITE("insert_overwrite"),
        SELECT("select"),
        DROP_TABLE("drop_table"),
        DROP_VIEW("drop_view"),
        ALTER_TABLE("alter_table"),
        CREATE_TABLE("create_table"),
        CTAS("ctas"),
        CTE("cte"),
        CREATE_VIEW("create_view"),
        UNKNOWN("unknown");

        public Query value;

        Type(String type) {
            this(new Query(type.toLowerCase()));
        }

        Type(Query type) {
            this.value = type;
        }
    }

    public Query(String value) throws IllegalArgumentException {
        this.type = Type.valueOf(value);
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
