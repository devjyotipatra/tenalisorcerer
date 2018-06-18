package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class JoinNode extends BaseAstNode {

    public final BaseAstNode leftNode;
    public final BaseAstNode rightNode;
    public final BaseAstNodeList joinCondition;

    @JsonCreator
    public JoinNode(@JsonProperty("left") BaseAstNode leftNode,
                    @JsonProperty("right") BaseAstNode rightNode,
                    @JsonProperty("condition") BaseAstNodeList joinCondition) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.joinCondition = joinCondition;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        visitor.visit(this);
    }

}
