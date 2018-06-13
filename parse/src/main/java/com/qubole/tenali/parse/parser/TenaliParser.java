package com.qubole.tenali.parse.parser;

import java.io.IOException;

/**
 * Created by devjyotip on 5/28/18.
 */
public interface TenaliParser {

  public void parse(String command, QueryType qt) throws IOException;

  public static enum QueryType {
    HIVE,
    SPARK_SQL,
    SPARK_SCALA,
    SPARK_PYTHON,
    SPARK_CLI,
    SPARK_R,
    ANSI_SQL
  }
}
