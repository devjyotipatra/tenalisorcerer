package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;

public interface AstTransformer<T> {

    public TenaliAstNode transform(T ast);
}
