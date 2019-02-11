package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.JoinNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

public class JoinBaseVisitor extends TenaliAstBaseVisitor {

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        if(!(node instanceof JoinNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return node;
        }

        JoinNode join = (JoinNode) node;

        join.leftNode.accept(this);
        join.rightNode.accept(this);

        join.joinCondition.accept(this);

        return node;
    }
}
