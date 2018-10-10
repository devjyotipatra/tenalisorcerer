package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.TenaliAstNodeVisitor;

public class JoinNode extends TenaliAstNode {
    public final String joinType;
    public final TenaliAstNode leftNode;
    public final TenaliAstNode rightNode;
    public final TenaliAstNode joinCondition;

    @JsonCreator
    public JoinNode(@JsonProperty("jointype") String joinType,
                    @JsonProperty("left") TenaliAstNode leftNode,
                    @JsonProperty("right") TenaliAstNode rightNode,
                    @JsonProperty("condition") TenaliAstNode joinCondition) {
        this.joinType = joinType;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.joinCondition = joinCondition;
    }

    @Override
    public void accept(TenaliAstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public static class  JoinBuilder implements Builder<TenaliAstNode> {
        String joinType;
        TenaliAstNode leftNode;
        TenaliAstNode rightNode;
        TenaliAstNode joinCondition;

        @Override
        public JoinNode build() {
            return new JoinNode(joinType, leftNode, rightNode, joinCondition);
        }

        public TenaliAstNode getLeftNode() {
            return leftNode;
        }

        public void setLeftNode(TenaliAstNode leftNode) {
            this.leftNode = leftNode;
        }

        public TenaliAstNode getRightNode() {
            return rightNode;
        }

        public void setRightNode(TenaliAstNode rightNode) {
            this.rightNode = rightNode;
        }

        public TenaliAstNode getJoinCondition() {
            return joinCondition;
        }

        public void setJoinCondition(TenaliAstNode joinCondition) {
            this.joinCondition = joinCondition;
        }

        public String getJoinType() {
            return joinType;
        }

        public void setJoinType(String joinType) {
            this.joinType = joinType;
        }
    }

}
