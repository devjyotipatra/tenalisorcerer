package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.DdlNode;
import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;

public class DdlNodeVisitor extends TenaliAstNodeVisitor {

    @Override
    public void visit(TenaliAstNode node) {
        if(!(node instanceof DdlNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return;
        }

        DdlNode ddl = (DdlNode) node;

        ddl.tableNode.accept(this);

        if(ddl.ctasNode != null) {
            ddl.ctasNode.accept(this);
        } else if(ddl.alterNode != null) {
            ddl.alterNode.accept(this);
        }
    }
}
