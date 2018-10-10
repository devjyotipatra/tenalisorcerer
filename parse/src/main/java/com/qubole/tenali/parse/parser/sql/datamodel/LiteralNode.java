package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.TenaliAstNodeVisitor;

public class LiteralNode extends TenaliAstNode {
    public Object value;
    @JsonCreator
    public LiteralNode(@JsonProperty("value") Object value) {
        this.value = value;
    }

    @Override
    public void accept(TenaliAstNodeVisitor visitor) {
        return;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
