package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandType;
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

    protected abstract void prepareLexer(String commandText);

    protected abstract void prepareParser();


    public void submit(CommandType.Type commandType, String command) throws IOException {
        CommandContext rootCtx = null;
        if(lexer !=  null && lexer.getLexerType() == commandType) {
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

    public static class TenaliParserBuilder {

        public build() {

        }
    }
}
