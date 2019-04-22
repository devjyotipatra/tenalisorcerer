package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;

public class LiteralNode extends TenaliAstNode {
    public String value;
    @JsonCreator
    public LiteralNode(@JsonProperty("value") String value) {
        this.value = value;
    }

    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
