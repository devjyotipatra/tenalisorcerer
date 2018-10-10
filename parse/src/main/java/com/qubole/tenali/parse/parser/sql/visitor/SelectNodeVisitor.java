package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.SelectNode;
import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;

public class SelectNodeVisitor extends TenaliAstNodeVisitor {

    @Override
    public void visit(TenaliAstNode node) {
        if(!(node instanceof SelectNode)) {
            System.out.println("Error:  node is not instance of selectnode");
            return;
        }

        SelectNode select = (SelectNode) node;

        if(select.hasWhere()) {
            select.where.accept(this);
        }

        if(select.hasGroupBy()) {
            for (TenaliAstNode n : select.groupBy.getOperandlist()) {
                n.accept(this);
            }
        }

        select.from.accept(this);

        if(select.hasWith()) {
            for (TenaliAstNode n : select.with.getOperandlist()) {
                n.accept(this);
            }
        }

        for (TenaliAstNode n : select.columns.getOperandlist()) {
            n.accept(this);
        }

        for (TenaliAstNode n : select.keywords.getOperandlist()) {
            n.accept(this);
        }

        if(select.hasHaving()) {
            select.having.accept(this);
        }

        if(select.hasWindow()) {
            for (TenaliAstNode n : select.windowDecls.getOperandlist()) {
                n.accept(this);
            }
        }
    }
}
