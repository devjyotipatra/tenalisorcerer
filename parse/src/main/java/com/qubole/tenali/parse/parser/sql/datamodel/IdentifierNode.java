package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.TenaliAstNodeVisitor;

public class IdentifierNode extends TenaliAstNode {
    public String name;
    @JsonCreator
    public IdentifierNode(@JsonProperty("name") String name) {
        this.name = name;
    }


    @Override
    public void accept(TenaliAstNodeVisitor visitor) {
        return;
    }

    @Override
    public String toString() {
        return name;
    }
}