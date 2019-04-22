package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.TenaliAstBaseTransformer;


public class IdentifierNode extends TenaliAstNode {
    public String name;
    @JsonCreator
    public IdentifierNode(@JsonProperty("name") String name) {
        this.name = name;
    }


    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }
}