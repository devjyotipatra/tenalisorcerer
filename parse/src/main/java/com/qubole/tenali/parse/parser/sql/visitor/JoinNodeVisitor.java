package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.JoinNode;
import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;

public class JoinNodeVisitor extends TenaliAstNodeVisitor {
    @Override
    public void visit(TenaliAstNode node) {
        if(!(node instanceof JoinNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return;
        }

        JoinNode join = (JoinNode) node;

        join.leftNode.accept(this);
        join.rightNode.accept(this);

        join.joinCondition.accept(this);
    }
}
