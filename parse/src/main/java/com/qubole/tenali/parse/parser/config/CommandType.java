package com.qubole.tenali.parse.parser.config;

public final class CommandType extends TenaliType<CommandType.Type> {

    Type type;

    public enum Type {
        HIVE,
        PRESTO,
        SQL,
        BASH,
        SPARK_SCALA,
        SPARK_PYTHON,
        SPARK_CLI,
        SPARK_R
    }

    public CommandType(Type value) throws IllegalArgumentException {
        super(value);
    }

    public Type getType() {
        return type;
    }

    @Override public boolean equals(Object other) {
        CommandType o = (CommandType) other;
        return o.type == this.type;
    }

    public String toString() {
        return type.toString();
    }
}
