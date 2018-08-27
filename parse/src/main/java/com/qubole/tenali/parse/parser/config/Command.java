package com.qubole.tenali.parse.parser.config;

public class Command {

    Type type = null;

    public enum Type {
        BASH("bash"),
        HIVE("hive"),
        SPARK_SQL("spark_sql"),
        SPARK_SCALA("spark_scala"),
        SPARK_PYTHON("spark_python"),
        SPARK_CLI("spark_cli"),
        SPARK_R("spark_r"),
        PRESTO("presto"),
        ANSI_SQL("ansi_sql");

        public Command value;

        Type(String type) {
            this(new Command(type.toLowerCase()));
        }

        Type(Command type) {
            this.value = type;
        }
    }

    public Command(String value) throws IllegalArgumentException {
        this.type = Type.valueOf(value);
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
