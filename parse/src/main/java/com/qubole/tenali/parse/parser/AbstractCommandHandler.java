package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandContext;

import java.io.IOException;

public abstract class AbstractCommandHandler {

    TenaliLexer lexer;

    TenaliParser parser;

    public AbstractCommandHandler(TenaliParser parser) {
        this(null, parser);
    }

    public AbstractCommandHandler(TenaliLexer lexer, TenaliParser parser) {
        this.lexer = lexer;
        this.parser = parser;
    }

    public abstract void prepareLexer(String commandText);

    public abstract void prepareParser();


    public void submit(String command) throws IOException {
        CommandContext rootCtx = null;
        if(lexer !=  null) {
            prepareLexer(command);
            rootCtx = lexer.getRootContext();
        }

        if(rootCtx != null) {
            CommandContext qctx = rootCtx;

            while(qctx != null) {
                String sqlStmt = qctx.getStmt();
                parser.parse(sqlStmt);

                qctx = qctx.getChild();
            }
        }
    }

    public TenaliLexer getLexer() {
        return lexer;
    }

    public TenaliParser getParser() {
        return parser;
    }
}
