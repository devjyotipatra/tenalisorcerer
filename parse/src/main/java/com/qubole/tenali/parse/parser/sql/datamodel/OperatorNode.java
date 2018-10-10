package com.qubole.tenali.parse.parser.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.TenaliAstNodeVisitor;


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
    public void accept(TenaliAstNodeVisitor visitor) {
        visitor.visit(this);
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
