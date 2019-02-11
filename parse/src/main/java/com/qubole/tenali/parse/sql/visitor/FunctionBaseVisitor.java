package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.FunctionNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;


public class FunctionBaseVisitor extends TenaliAstBaseVisitor {

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        if(!(node instanceof FunctionNode)) {
            System.out.println("Error:  node is not instance of FunctionNode");
            return node;
        }

        FunctionNode function = (FunctionNode) node;

        for(TenaliAstNode arg : function.arguments.getOperandlist()) {
            /*if(arg instanceof ColumnNode) {
                arg.accept(this);
            }*/
        }

        return node;
    }
}
