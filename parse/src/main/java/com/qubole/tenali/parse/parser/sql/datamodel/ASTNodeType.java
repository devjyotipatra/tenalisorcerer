package com.qubole.tenali.parse.parser.sql.datamodel;

public enum ASTNodeType {
    OTHER,
    SELECT,
    TABLE,
    JOIN,
    IDENTIFIER,
    LITERAL,
    FUNCTION,
    INSERT,
    CREATE,
    DELETE,
    UPDATE,
    ORDER_BY,
    GROUP_BY,
    WITH,
    UNION,
    EXCEPT,
    INTERSECT,

}