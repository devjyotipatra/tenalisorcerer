package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.DDLNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

public class DdlBaseVisitor extends TenaliAstBaseVisitor {

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        if(!(node instanceof DDLNode)) {
            System.out.println("Error:  node is not instance of JoinNode");
            return node;
        }

        DDLNode ddl = (DDLNode) node;

        ddl.tableNode.accept(this);

       /* if(ddl.ctasNode != null) {
            ddl.ctasNode.accept(this);
        } else if(ddl.alterNode != null) {
            ddl.alterNode.accept(this);
        }*/

        return node;
    }
}
