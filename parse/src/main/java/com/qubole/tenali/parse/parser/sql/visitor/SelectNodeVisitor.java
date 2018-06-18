package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.BaseAstNode;
import com.qubole.tenali.parse.parser.sql.datamodel.SelectNode;

public class SelectNodeVisitor extends BaseAstNodeVisitor {

    @Override
    public void visit(BaseAstNode node) {
        if(!(node instanceof SelectNode)) {
            System.out.println("Error:  node is not instance of selectnode");
            return;
        }

        SelectNode select = (SelectNode) node;

        if(select.hasWhere()) {
            select.where.accept(this);
        }

        if(select.hasOrderBy()) {
            for (BaseAstNode n : select.orderBy.getOperandlist()) {
                n.accept(this);
            }
        }

        if(select.hasGroupBy()) {
            for (BaseAstNode n : select.groupBy.getOperandlist()) {
                n.accept(this);
            }
        }

        select.from.accept(this);

        if(select.hasWith()) {
            for (BaseAstNode n : select.with.getOperandlist()) {
                n.accept(this);
            }
        }

        for (BaseAstNode n : select.columns.getOperandlist()) {
            n.accept(this);
        }

        for (BaseAstNode n : select.keywords.getOperandlist()) {
            n.accept(this);
        }

        if(select.hasHaving()) {
            select.having.accept(this);
        }

        if(select.hasWindow()) {
            for (BaseAstNode n : select.windowDecls.getOperandlist()) {
                n.accept(this);
            }
        }
    }
}
