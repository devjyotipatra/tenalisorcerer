package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;


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
    public Object accept(TenaliAstBaseTransformer visitor) {
        return error;
    }

    @Override
    public String toString() {
        return error;
    }
}