package com.qubole.tenali.parse.parser.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;


public class OperatorNode extends BaseAstNode {
    public String operator;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("operator: ").append(operator).append("\n")
                .append("operands: ").append(operands.toString());
        return sb.toString();
    }

    public static class  OperatorBuilder implements Builder {
        String operator;
        BaseAstNodeList operands;

        @Override
        public BaseAstNode build() {
            return new OperatorNode(operator, operands);
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public BaseAstNodeList getOperands() {
            return operands;
        }

        public void setOperands(BaseAstNodeList operands) {
            this.operands = operands;
        }
    }
}
