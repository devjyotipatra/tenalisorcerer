package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.TenaliTransformer;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.sql.datamodel.*;



public abstract class TenaliAstBaseTransformer<T> extends TenaliTransformer<TenaliAstNode, T> {
    protected CommandContext ctx;

    public TenaliAstBaseTransformer() {
        super(TenaliAstNode.class);
    }

    @Override
    public T transform(TenaliAstNode ast, CommandContext ctx) {
        T root = null;
        this.ctx = ctx;

        QueryType queryType = ctx.getQueryType();

        if(queryType == QueryType.SELECT
                || queryType == QueryType.CTE
                || queryType == QueryType.CTAS
                || queryType == QueryType.INSERT_OVERWRITE
                || queryType == QueryType.CREATE_TABLE
                || queryType == QueryType.DROP_TABLE
                || queryType == QueryType.ALTER_TABLE) {
            root = visit(ast);
        }

        return root;

    }

    public abstract T visit(TenaliAstNode node);

}
