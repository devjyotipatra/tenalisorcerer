package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.CommandType;

public interface TenaliLexer<T> {
    CommandContext getRootContext();

    void extract(String command);

    CommandType getCommandType();
}
