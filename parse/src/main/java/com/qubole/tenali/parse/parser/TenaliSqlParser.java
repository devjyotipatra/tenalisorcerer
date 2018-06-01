package com.qubole.tenali.parse.parser;


import antlr4.QDSCommandLexer;
import com.qubole.tenali.parse.Parsers;
import com.qubole.tenali.parse.TenaliParser;
import com.qubole.tenali.parse.exception.CommandErrorListener;
import com.qubole.tenali.parse.exception.CommandParseError;
import com.qubole.tenali.parse.exception.SQLSyntaxError;

import antlr4.QDSCommandParser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by devjyotip on 5/29/18.
 */
public class TenaliSqlParser implements TenaliParser {

  public void parse(String command) throws IOException {
    InputStream antlrInputStream =
        new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));

    QDSCommandLexer lexer =
        new QDSCommandLexer(CharStreams.fromStream(antlrInputStream, StandardCharsets.UTF_8));

    QDSCommandParser parser = new QDSCommandParser(new CommonTokenStream(lexer));
    parser.setBuildParseTree(true);
    parser.removeErrorListeners();
    parser.addErrorListener(new CommandErrorListener());

    ParseTree tree;
    try {
      tree = parser.parse();
    } catch (CommandParseError e) {
      throw new SQLSyntaxError(e);
    }

    System.out.println("---- command parsing ----");
    Parsers.CommandParser commandParser = new Parsers.CommandParser();
    commandParser.visit(tree);
  }
}
