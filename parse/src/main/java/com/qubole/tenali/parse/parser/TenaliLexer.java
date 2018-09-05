package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.config.CommandContext;

public interface TenaliLexer<T> {
    public CommandContext getRootContext();

    public CommandType.Type getLexerType();
}
