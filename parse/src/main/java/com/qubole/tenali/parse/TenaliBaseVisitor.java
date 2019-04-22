package com.qubole.tenali.parse;


import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;


public interface TenaliBaseVisitor<S, T> {

    T transform(S ast, CommandContext ctx);

    String getIdentifier();

    Class getType();
}