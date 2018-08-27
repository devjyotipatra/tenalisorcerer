package com.qubole.tenali.parse.parser;

import antlr4.QDSCommandLexer;
import antlr4.QDSCommandParser;
import com.qubole.tenali.parse.exception.CommandErrorListener;
import com.qubole.tenali.parse.exception.CommandParseError;
import com.qubole.tenali.parse.exception.SQLSyntaxError;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TenaliSqlCommandHandler extends AbstractCommandHandler {

    public TenaliSqlCommandHandler(TenaliParser parser) {
        super(new TenaliSqlCommandLexer(), parser);
    }


    public TenaliSqlCommandHandler(TenaliLexer lexer, TenaliParser parser) {
        super(lexer, parser);
    }


    @Override
    public void prepareLexer(String commandText) {
        TenaliLexer tenaliLexer = getLexer();

        try {
            InputStream antlrInputStream =
                    new ByteArrayInputStream(commandText.getBytes(StandardCharsets.UTF_8));

            QDSCommandLexer lexer =
                    new QDSCommandLexer(CharStreams.fromStream(antlrInputStream, StandardCharsets.UTF_8));

            QDSCommandParser parser = new QDSCommandParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(true);
            parser.removeErrorListeners();
            parser.addErrorListener(new CommandErrorListener());

            ParseTree tree = parser.parse();

            System.out.println("---- command parsing ----");

            ((TenaliSqlCommandLexer) tenaliLexer).visit(tree);

        } catch (CommandParseError e) {
            throw new SQLSyntaxError(e);
        } catch (IOException io) {

        }
    }


    @Override
    public void prepareParser() { }
}
