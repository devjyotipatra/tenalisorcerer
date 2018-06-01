package com.qubole.tenali.parse.exception;

/**
 * Created by devjyotip on 5/12/18.
 */
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import org.apache.commons.lang.StringUtils;

public class CommandErrorListener extends BaseErrorListener {
  public void syntaxError(Recognizer<?, ?> recognizer,
                          Object offendingSymbol,
                          int line, int charPositionInLine,
                          String msg,
                          RecognitionException e) {
    CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
    String input = tokens.getTokenSource().getInputStream().toString();
    String token = offendingSymbol.toString();
    String[] lines = StringUtils.splitPreserveAllTokens(input, '\n');
    String errorLine = lines[line - 1];

    throw new CommandParseError(token, line, charPositionInLine, msg, errorLine);
  }
}