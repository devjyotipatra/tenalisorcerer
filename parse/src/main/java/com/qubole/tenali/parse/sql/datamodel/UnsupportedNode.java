package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class UnsupportedNode extends TenaliAstNode {
    public final String unsupported;

    @JsonCreator
    public UnsupportedNode(@JsonProperty("unsupported") String error) {
        unsupported = error;
    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return unsupported;
    }
}