package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.DdlNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

public class DdlBaseVisitor extends TenaliAstBaseVisitor {

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        if(!(node instanceof DdlNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return node;
        }

        DdlNode ddl = (DdlNode) node;

        ddl.tableNode.accept(this);

        if(ddl.ctasNode != null) {
            ddl.ctasNode.accept(this);
        } else if(ddl.alterNode != null) {
            ddl.alterNode.accept(this);
        }

        return node;
    }
}
