package com.qubole.tenali.parse.lexer;

import com.qubole.tenali.parse.config.CommandType;

public class HiveCommandLexer extends SqlCommandLexer {

    public HiveCommandLexer() {
        super(CommandType.HIVE);
    }
}
