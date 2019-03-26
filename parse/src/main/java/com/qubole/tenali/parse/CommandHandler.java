package com.qubole.tenali.parse;

import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.lexer.DummyLexer;

public abstract class CommandHandler {

    CommandType commandType;

    TenaliLexer lexer;

    TenaliParser parser;

    ImmutableList.Builder<AstTransformer> transformerBuilder = new ImmutableList.Builder();

    public CommandHandler(CommandType commandType) {
        this.commandType = commandType;
    }


    protected static void extractQueries(String command) {
        TenaliLexer lexer = new DummyLexer();
        lexer.extract(command);
    }

    protected static void extractQueries(TenaliLexer lexer, String command) {
        lexer.extract(command);
    }


    protected static void prepareParser(TenaliParser parser) {
        parser.prepare();
    }

    public CommandHandler setLexer(TenaliLexer lexer) {
        this.lexer = lexer;
        return this;
    }

    public CommandHandler setParser(TenaliParser parser) {
        this.parser = parser;
        return this;
    }

    public CommandHandler setTransformer(AstTransformer transformer) {
        this.transformerBuilder.add(transformer);
        return this;
    }

    abstract public CommandContext build(String command);

}
