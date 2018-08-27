package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandContext;

public interface TenaliLexer<T> {

    CommandContext root = null;

    public CommandContext getRootContext();
}
