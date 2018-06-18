package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.BaseAstNode;
import com.qubole.tenali.parse.parser.sql.datamodel.JoinNode;

public class JoinNodeVisitor extends BaseAstNodeVisitor {
    @Override
    public void visit(BaseAstNode node) {
        if(!(node instanceof JoinNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return;
        }

        JoinNode join = (JoinNode) node;

        join.leftNode.accept(this);
        join.rightNode.accept(this);

        for(BaseAstNode j : join.joinCondition.getOperandlist()) {
            j.accept(this);
        }
    }
}
