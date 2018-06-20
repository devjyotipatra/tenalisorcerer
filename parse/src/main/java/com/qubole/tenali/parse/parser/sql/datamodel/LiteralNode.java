package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class LiteralNode extends BaseAstNode {
    public Object value;
    @JsonCreator
    public LiteralNode(@JsonProperty("value") Object value) {
        this.value = value;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
