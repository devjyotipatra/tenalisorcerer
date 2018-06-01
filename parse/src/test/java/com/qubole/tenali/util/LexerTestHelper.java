package com.qubole.tenali.util;

import com.qubole.tenali.parse.TenaliCommandParser;

import java.io.IOException;

/**
 * Created by devjyotip on 5/10/18.
 */
public class LexerTestHelper {

  public static void parse(String command) throws IOException {
    long commandId = 12345l;
    TenaliCommandParser parser = new TenaliCommandParser(commandId);
    parser.submit(command);
  }
}
