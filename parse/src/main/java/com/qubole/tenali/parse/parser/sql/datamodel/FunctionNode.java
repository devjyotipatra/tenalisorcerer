package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class FunctionNode extends BaseAstNode {

    public String functionName;
    public final BaseAstNodeList arguments;

    @JsonCreator
    public FunctionNode(@JsonProperty("functionName") String functionName,
                      @JsonProperty("arguments") BaseAstNodeList arguments) {
        this.functionName = functionName;
        this.arguments = arguments;

    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
