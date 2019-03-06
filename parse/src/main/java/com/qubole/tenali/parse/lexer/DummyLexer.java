package com.qubole.tenali.parse.lexer;

import com.qubole.tenali.parse.TenaliLexer;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.CommandType;

public class DummyLexer implements TenaliLexer {

    final CommandType commandType = CommandType.UNKNOWN;

    CommandContext root;

    @Override
    public void extract(String command) {
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
