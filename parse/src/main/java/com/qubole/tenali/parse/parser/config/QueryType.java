package com.qubole.tenali.parse.parser.config;

public enum QueryType  {

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
    UNKNOWN
}