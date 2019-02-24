package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class ErrorNode extends TenaliAstNode {
    public String error;
    //public String unsupportedNodeType;
    @JsonCreator
    public ErrorNode(@JsonProperty("error") String error/*,
                     @JsonProperty("unsupportednode") String unsupportedNodeType*/) {
        this.error = error;
        //this.unsupportedNodeType = unsupportedNodeType;
    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return error;
    }
}