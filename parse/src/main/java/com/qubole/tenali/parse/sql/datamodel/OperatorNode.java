package com.qubole.tenali.parse.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;


public class OperatorNode extends TenaliAstNode {
    public String operator;
    public final TenaliAstNodeList operands;

    @JsonCreator
    public OperatorNode(@JsonProperty("operator") String operator,
                        @JsonProperty("operands") TenaliAstNodeList operands) {
        this.operator = operator;
        this.operands = operands;
    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(operands);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("operator: ").append(operator).append("\n")
                .append("operands: ").append(operands.toString());
        return sb.toString();
    }

    public static class  OperatorBuilder implements Builder<TenaliAstNode> {
        String operator;
        TenaliAstNodeList operands;

        public OperatorBuilder() {}

        public OperatorBuilder(OperatorNode node) {
            this.operator = node.operator;
            this.operands = node.operands;
        }

        @Override
        public TenaliAstNode build() {
            return new OperatorNode(operator, operands);
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public TenaliAstNodeList getOperands() {
            return operands;
        }

        public void setOperands(TenaliAstNodeList operands) {
            this.operands = operands;
        }
    }
}