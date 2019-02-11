package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.QueryType;


public interface AstTransformer<S, T> {

    public T transform(S ast, QueryType queryType);

    public String getIdentifier();

    public Class getType();
}
