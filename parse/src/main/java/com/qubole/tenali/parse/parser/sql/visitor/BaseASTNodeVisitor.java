package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.BaseASTNode;
import com.qubole.tenali.parse.parser.sql.datamodel.SelectNode;

public abstract class BaseASTNodeVisitor {
    public final void visit(BaseASTNode node) {
        visitNext(node);
    }

    public void visitNext(BaseASTNode node) {
        if(node instanceof SelectNode) {
            SelectNode select = (SelectNode) node;
            visitSelectNode(select);
        }
    }


    public void visitSelectNode(SelectNode select) {
        if(select.hasWhere()) {
            select.where.accept(this);
        }

        if(select.hasOrderBy()) {
            for (BaseASTNode n : select.orderBy.getOperandlist()) {
                n.accept(this);
            }
        }

        if(select.hasGroupBy()) {
            for (BaseASTNode n : select.groupBy.getOperandlist()) {
                n.accept(this);
            }
        }

        select.from.accept(this);

        if(select.hasWith()) {
            for (BaseASTNode n : select.with.getOperandlist()) {
                n.accept(this);
            }
        }

        for (BaseASTNode n : select.columns.getOperandlist()) {
            n.accept(this);
        }

        for (BaseASTNode n : select.keywords.getOperandlist()) {
            n.accept(this);
        }

        if(select.hasHaving()) {
            select.having.accept(this);
        }

        if(select.hasWindow()) {
            for (BaseASTNode n : select.windowDecls.getOperandlist()) {
                n.accept(this);
            }
        }
    }
}
