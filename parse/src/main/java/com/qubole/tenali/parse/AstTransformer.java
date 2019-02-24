package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryType;


public interface AstTransformer<S, T> {

    public T transform(S ast, CommandContext ctx);

    public String getIdentifier();

    public Class getType();
}
