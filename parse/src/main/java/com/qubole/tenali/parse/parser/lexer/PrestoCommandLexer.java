package com.qubole.tenali.parse.parser.lexer;

import com.qubole.tenali.parse.parser.config.CommandType;

public class PrestoCommandLexer extends SqlCommandLexer {

    public PrestoCommandLexer() {
        super(CommandType.PRESTO);
    }
}