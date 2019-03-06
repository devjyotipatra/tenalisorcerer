package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.CommandType;

public interface TenaliLexer<T> {
    public CommandContext getRootContext();

    public void extract(String command);

    public CommandType getCommandType();
}
