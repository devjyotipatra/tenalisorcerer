package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;


public class UnsupportedNode extends TenaliAstNode {
    public final String unsupported;

    @JsonCreator
    public UnsupportedNode(@JsonProperty("unsupported") String error) {
        unsupported = error;
    }

    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        return unsupported;
    }

    @Override
    public String toString() {
        return unsupported;
    }
}