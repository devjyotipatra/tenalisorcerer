package com.qubole.tenali.parse.parser;

import java.io.IOException;

/**
 * Created by devjyotip on 5/28/18.
 */
public class TenaliCommandParser_tmp extends Parsers {

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
        case "hive":
          createSqlParser().parse(command, TenaliParser.QueryType.HIVE);
          break;
        case "unknown-source":
        case "mysql":
        case "presto":
          createSqlParser().parse(command, TenaliParser.QueryType.ANSI_SQL);
          break;
        case "spark":
          switch (language) {
            case "sql":
              createSqlParser().parse(command, TenaliParser.QueryType.SPARK_SQL);
              break;
            case "scala":
              //createSqlParser().parse(command, TenaliParser.QueryType.SPARK_SCALA);
              break;
            case "python":
              //createSqlParser().parse(command, TenaliParser.QueryType.SPARK_PYTHON);
              break;
            case "command_line":
              //createSqlParser().parse(command, TenaliParser.QueryType.SPARK_CLI);
              break;
            case "r":
              //createSqlParser().parse(command, TenaliParser.QueryType.SPARK_R);
          }
      }
    } catch(IOException ex) {
      System.out.println("Error in parsing   " + command + "\n" +
                         "Source " + source + "\n" +
                         "Language " + language);
    }
  }
}
