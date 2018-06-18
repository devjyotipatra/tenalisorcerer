package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;



public class TableNode extends BaseAstNode {
    public String schemaName;
    public String tableName;
    @JsonCreator
    public TableNode(@JsonProperty("schemaName") String schemaName,
                     @JsonProperty("tableName") String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }
}
