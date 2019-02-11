package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class IdentifierNode extends TenaliAstNode {
    public String name;
    @JsonCreator
    public IdentifierNode(@JsonProperty("name") String name) {
        this.name = name;
    }


    @Override
    public void accept(TenaliAstBaseVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }
}