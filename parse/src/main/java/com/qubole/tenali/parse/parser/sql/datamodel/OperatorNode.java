package com.qubole.tenali.parse.parser.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class OperatorNode extends BaseAstNode {
    public final String operator;
    public final BaseAstNodeList operands;

    @JsonCreator
    public OperatorNode(@JsonProperty("operator") String operator,
                        @JsonProperty("operands") BaseAstNodeList operands) {
        this.operator = operator;
        this.operands = operands;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
