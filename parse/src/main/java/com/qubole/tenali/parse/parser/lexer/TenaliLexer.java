package com.qubole.tenali.parse.parser.lexer;

import com.qubole.tenali.parse.parser.config.CommandContext;
import com.qubole.tenali.parse.parser.config.CommandType;

public interface TenaliLexer<T> {
    public CommandContext getRootContext();

    public void prepare(String command);

    public CommandType getCommandType();
}
