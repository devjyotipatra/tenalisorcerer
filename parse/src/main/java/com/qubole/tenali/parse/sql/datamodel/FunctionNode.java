package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class FunctionNode extends TenaliAstNode {

    public String functionName;
    public final TenaliAstNodeList arguments;

    @JsonCreator
    public FunctionNode(@JsonProperty("functionName") String functionName,
                      @JsonProperty("arguments") TenaliAstNodeList arguments) {
        this.functionName = functionName;
        this.arguments = arguments;

    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(arguments);
    }

    public static class  FunctionBuilder implements Builder<TenaliAstNode> {
        String functionName;
        TenaliAstNodeList arguments;

        public TenaliAstNode build() {
            return new FunctionNode(functionName, arguments);
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public TenaliAstNodeList getArguments() {
            return arguments;
        }

        public void setArguments(TenaliAstNodeList arguments) {
            this.arguments = arguments;
        }
    }
}