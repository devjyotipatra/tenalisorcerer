package com.qubole.tenali.parse;

import java.io.IOException;

/**
 * Created by devjyotip on 5/28/18.
 */
public class TenaliCommandParser extends Parsers {

  public TenaliCommandParser(long queryId) {
    super(queryId);
  }

  public void submit(String command) {
    submit(command, "unknown-source", "sql");
  }

  public void submit(String command, String source) {
    submit(command, source, "sql");
  }

  public void submit(String command, String source, String language) {
    source = source.toLowerCase();
    language = language.toLowerCase();

    try {
      switch (source) {
        case "unknown-source":
        case "hive":
        case "presto":
          createSqlParser().parse(command);
          break;
        case "spark":
          switch (language) {
            case "sql":
              createSqlParser().parse(command);
              break;
            case "scala":
              //parseScalaCommand(command);
              break;
            case "python":
              //parsePythonCommand(command);
              break;
            case "command_line":
              //parseCLICommand(command);
              break;
            case "r":
              //parseRCommand(command);
              break;
          }
      }
    } catch(IOException ex) {
      System.out.println("Error in parsing   " + command + "\n" +
                         "Source " + source + "\n" +
                         "Language " + language);
    }
  }
}
