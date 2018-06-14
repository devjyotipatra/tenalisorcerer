package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.*;

public abstract class BaseAstNodeVisitor {
    public final void visit(BaseAstNode node) {
        visitNext(node);
    }

    public void visitNext(BaseAstNode node) {
        if(node instanceof SelectNode) {
            SelectNode select = (SelectNode) node;
            visitSelectNode(select);
        }
        else if(node instanceof DDLNode) {
            DDLNode ddl = (DDLNode) node;
            visitDDLNode(ddl);
        }
        else if(node instanceof JoinNode) {
            JoinNode join = (JoinNode) node;
            visitJoinNode(join);
        }
        else if(node instanceof OperatorNode) {
            OperatorNode operator = (OperatorNode) node;
            visitOperatorNode(operator);
        }
    }


    public void visitSelectNode(SelectNode select) {
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

    public void visitDDLNode(DDLNode ddl) {
        ddl.tableNode.accept(this);

        if(ddl.ctasNode != null) {
            ddl.ctasNode.accept(this);
        } else if(ddl.alterNode != null) {
            ddl.alterNode.accept(this);
        }
    }

    public void visitJoinNode(JoinNode join) {
        join.leftNode.accept(this);
        join.rightNode.accept(this);
        join.joinCondition.accept(this);
    }

    public void visitOperatorNode(OperatorNode operator) {
        for (BaseAstNode o : operator.operands.getOperandlist()) {
            o.accept(this);
        }
    }
}
