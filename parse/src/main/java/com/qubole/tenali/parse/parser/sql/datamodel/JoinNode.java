package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class JoinNode extends BaseAstNode {

    public final BaseAstNode leftNode;
    public final BaseAstNode rightNode;
    public final BaseAstNode joinCondition;

    @JsonCreator
    public JoinNode(@JsonProperty("left") BaseAstNode leftNode,
                    @JsonProperty("right") BaseAstNode rightNode,
                    @JsonProperty("condition") BaseAstNode joinCondition) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.joinCondition = joinCondition;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public static class  JoinBuilder implements Builder {
        BaseAstNode leftNode;
        BaseAstNode rightNode;
        BaseAstNode joinCondition;

        @Override
        public JoinNode build() {
            return new JoinNode(leftNode, rightNode, joinCondition);
        }

        public BaseAstNode getLeftNode() {
            return leftNode;
        }

        public void setLeftNode(BaseAstNode leftNode) {
            this.leftNode = leftNode;
        }

        public BaseAstNode getRightNode() {
            return rightNode;
        }

        public void setRightNode(BaseAstNode rightNode) {
            this.rightNode = rightNode;
        }

        public BaseAstNode getJoinCondition() {
            return joinCondition;
        }

        public void setJoinCondition(BaseAstNode joinCondition) {
            this.joinCondition = joinCondition;
        }
    }

}