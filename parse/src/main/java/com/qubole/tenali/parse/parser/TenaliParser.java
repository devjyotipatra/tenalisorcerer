package com.qubole.tenali.parse.parser;

import java.io.IOException;

/**
 * Created by devjyotip on 5/28/18.
 */
public interface TenaliParser {

    public static enum CommandType {
        BASH,
        HIVE,
        SPARK_SQL,
        SPARK_SCALA,
        SPARK_PYTHON,
        SPARK_CLI,
        SPARK_R,
        PRESTO,
        ANSI_SQL
    }

   /* public static enum StatementType {
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

    }*/

    void parse(String command, CommandType ct) throws IOException;
}
