package com.qubole.tenali.parse.parser.lexer;

import com.qubole.tenali.parse.parser.config.CommandType;

public class HiveCommandLexer extends SqlCommandLexer {

    public HiveCommandLexer() {
        super(CommandType.HIVE);
    }
}
