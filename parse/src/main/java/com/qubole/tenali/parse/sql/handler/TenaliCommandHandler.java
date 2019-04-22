package com.qubole.tenali.parse.sql.handler;

import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.TenaliLexer;
import com.qubole.tenali.parse.TenaliParser;
import com.qubole.tenali.parse.TenaliTransformer;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.lexer.DummyLexer;

public abstract class TenaliCommandHandler {

    protected CommandType commandType;

    protected TenaliLexer lexer;

    protected TenaliParser parser;

    protected ImmutableList.Builder<TenaliTransformer> transformerBuilder = new ImmutableList.Builder();

    public TenaliCommandHandler(CommandType commandType) {
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

    public TenaliCommandHandler setLexer(TenaliLexer lexer) {
        this.lexer = lexer;
        return this;
    }

    public TenaliCommandHandler setParser(TenaliParser parser) {
        this.parser = parser;
        return this;
    }

    public TenaliCommandHandler setTransformer(TenaliTransformer transformer) {
        this.transformerBuilder.add(transformer);
        return this;
    }

    abstract public CommandContext build(String command);

}
