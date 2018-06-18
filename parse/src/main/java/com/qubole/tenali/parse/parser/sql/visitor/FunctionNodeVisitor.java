package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.BaseAstNode;
import com.qubole.tenali.parse.parser.sql.datamodel.ColumnNode;
import com.qubole.tenali.parse.parser.sql.datamodel.FunctionNode;


public class FunctionNodeVisitor extends BaseAstNodeVisitor {

    @Override
    public void visit(BaseAstNode node) {
        if(!(node instanceof FunctionNode)) {
            System.out.println("Error:  node is not instance of FunctionNode");
            return;
        }

        FunctionNode function = (FunctionNode) node;

        for(BaseAstNode arg : function.arguments.getOperandlist()) {
            if(arg instanceof ColumnNode) {
                arg.accept(this);
            }
        }
    }
}
