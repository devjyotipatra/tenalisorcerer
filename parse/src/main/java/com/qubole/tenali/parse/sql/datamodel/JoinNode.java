package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;

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
    public Object accept(TenaliAstBaseTransformer visitor) {
        Object[] arr = new Object[2];
        //arr[0] = leftNode.accept(visitor);
        //arr[1] = rightNode.accept(visitor);
        arr[0] = visitor.visit(leftNode);
        arr[1] = visitor.visit(rightNode);
        return arr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftNode.toString()).append("==")
                .append(joinType).append("==")
                .append(rightNode.toString()).append("==")
                .append(joinCondition.toString());

        return sb.toString();
    }

    public static class  JoinBuilder implements Builder<TenaliAstNode> {
        String joinType;
        TenaliAstNode leftNode;
        TenaliAstNode rightNode;
        TenaliAstNode joinCondition;

        public JoinBuilder() { }

        public JoinBuilder(JoinNode node) {
            this.joinType = node.joinType;
            this.leftNode = node.leftNode;
            this.rightNode = node.rightNode;
            this.joinCondition = node.joinCondition;
        }

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
