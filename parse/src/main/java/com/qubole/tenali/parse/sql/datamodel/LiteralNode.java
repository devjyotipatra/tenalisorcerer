package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class LiteralNode extends TenaliAstNode {
    public Object value;
    @JsonCreator
    public LiteralNode(@JsonProperty("value") Object value) {
        this.value = value;
    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
