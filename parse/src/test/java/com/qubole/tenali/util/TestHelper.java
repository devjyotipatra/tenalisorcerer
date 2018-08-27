package com.qubole.tenali.util;

import java.io.IOException;

/**
 * Created by devjyotip on 5/10/18.
 */
public class TestHelper {

  public static void parse(String command) throws IOException {
    long commandId = 12345l;
    String source = "mysql";
    TenaliCommandParser_tmp parser = new TenaliCommandParser_tmp(commandId);
    parser.submit(command, source);
  }

  public void cache(String dbName, String tableName) {

  }
}
