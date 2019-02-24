package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.AstBaseVisitor;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.sql.datamodel.*;

public abstract class TenaliAstBaseVisitor<T> extends AstBaseVisitor<TenaliAstNode, T> {

    protected String defaultDb = "default";

    protected CommandContext ctx;

    public TenaliAstBaseVisitor() {
        super(TenaliAstNode.class);
    }

    @Override
    public T transform(TenaliAstNode ast, CommandContext ctx) {
        T root = null;
        this.ctx = ctx;

        if(ctx.getQueryContext().getQueryType() == QueryType.SELECT
                || ctx.getQueryContext().getQueryType() == QueryType.CTE) {
            root = visit(ast);
        }

        return root;

    }

    public abstract T visit(TenaliAstNode node);

}
