package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class ErrorNode extends BaseAstNode {
    public String error;
    public String unsupportedNodeType;
    @JsonCreator
    public ErrorNode(@JsonProperty("error") String error,
                     @JsonProperty("unsupportednode") String unsupportedNodeType) {
        this.error = error;
        this.unsupportedNodeType = unsupportedNodeType;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }
}