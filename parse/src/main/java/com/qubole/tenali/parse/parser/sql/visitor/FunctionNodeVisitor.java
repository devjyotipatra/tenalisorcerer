package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.FunctionNode;
import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;


public class FunctionNodeVisitor extends TenaliAstNodeVisitor {

    @Override
    public void visit(TenaliAstNode node) {
        if(!(node instanceof FunctionNode)) {
            System.out.println("Error:  node is not instance of FunctionNode");
            return;
        }

        FunctionNode function = (FunctionNode) node;

        for(TenaliAstNode arg : function.arguments.getOperandlist()) {
            /*if(arg instanceof ColumnNode) {
                arg.accept(this);
            }*/
        }
    }
}
