package com.qubole.tenali.parse.parser.config;

public class Command {

    Type type = null;

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

    public Command(Type value) throws IllegalArgumentException {
        this.type = value;
    }

    public Type getType() {
        return type;
    }

    @Override public boolean equals(Object other) {
        Command o = (Command) other;
        return o.type == this.type;
    }

    public String toString() {
        return type.toString();
    }
}
