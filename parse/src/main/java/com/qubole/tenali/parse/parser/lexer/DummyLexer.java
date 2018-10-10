package com.qubole.tenali.parse.parser.lexer;

import com.qubole.tenali.parse.parser.config.CommandContext;
import com.qubole.tenali.parse.parser.config.CommandType;

public class DummyLexer implements TenaliLexer {

    final CommandType commandType = CommandType.UNKNOWN;

    CommandContext root;

    @Override
    public void prepare(String command) {
        root = new CommandContext();
        root.setStmt(command);
    }

    @Override
    public CommandContext getRootContext() {
        return root;
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
