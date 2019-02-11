package com.qubole.tenali.parse.lexer;

import com.qubole.tenali.parse.config.CommandType;

public class PrestoCommandLexer extends SqlCommandLexer {

    public PrestoCommandLexer() {
        super(CommandType.PRESTO);
    }
}