package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.AstBaseVisitor;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.sql.datamodel.*;
import com.qubole.tenali.parse.util.exception.NotImplementedException;

public abstract class TenaliAstBaseVisitor<T> extends AstBaseVisitor<TenaliAstNode, T> {

    public TenaliAstBaseVisitor() {
        super(TenaliAstNode.class);
    }

    @Override
    public T transform(TenaliAstNode ast, QueryType queryType) {
        T root = null;

        if(queryType == QueryType.SELECT) {
            root = visit(ast);
        }

        return root;

    }

    public abstract T visit(TenaliAstNode node);

    public T visitSelectNode(TenaliAstNode node) {
        throw new NotImplementedException();
    }

}
