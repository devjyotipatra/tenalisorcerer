package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.BaseAstNode;
import com.qubole.tenali.parse.parser.sql.datamodel.OperatorNode;

public class OperatorNodeVisitor extends BaseAstNodeVisitor {

    @Override
    public void visit(BaseAstNode node) {
        if(!(node instanceof OperatorNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return;
        }

        OperatorNode operator = (OperatorNode) node;

        for (BaseAstNode o : operator.operands.getOperandlist()) {
            o.accept(this);
        }
    }
}
