package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.sql.datamodel.*;


public abstract class SqlAstBaseTransformer<S> extends TenaliTransformer<S, TenaliAstNode> {
    protected CommandContext ctx;

    public SqlAstBaseTransformer(Class<S> clazz) {
        super(clazz);
    }


    public abstract TenaliAstNode transform(S node, CommandContext ctx);

}
