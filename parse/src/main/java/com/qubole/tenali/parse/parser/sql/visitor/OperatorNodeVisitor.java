package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.OperatorNode;
import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;

public class OperatorNodeVisitor extends TenaliAstNodeVisitor {

    @Override
    public void visit(TenaliAstNode node) {
        if(!(node instanceof OperatorNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return;
        }

        OperatorNode operator = (OperatorNode) node;

        for (TenaliAstNode o : operator.operands) {
            o.accept(this);
        }
    }
}
