package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.OperatorNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

public class OperatorBaseVisitor extends TenaliAstBaseVisitor {

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        if(!(node instanceof OperatorNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return node;
        }

        OperatorNode operator = (OperatorNode) node;

        for (TenaliAstNode o : operator.operands) {
            o.accept(this);
        }

        return node;
    }
}
