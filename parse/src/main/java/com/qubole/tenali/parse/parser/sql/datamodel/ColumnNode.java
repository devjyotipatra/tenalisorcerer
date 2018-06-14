package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class ColumnNode extends BaseAstNode {
    public final String schemaName;
    public final String tableName;
    public final String columnName;
    @JsonCreator
    public ColumnNode(@JsonProperty("schemaName") String schemaName,
                     @JsonProperty("tableName") String tableName,
                     @JsonProperty("columnName") String columnName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }
}
