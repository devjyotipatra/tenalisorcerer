package com.qubole.tenali.util;

import com.qubole.tenali.parse.parser.TenaliAnsiSqlParser;
import com.qubole.tenali.parse.parser.TenaliHiveSqlParser;
import com.qubole.tenali.parse.parser.TenaliSqlCommandHandler;
import com.qubole.tenali.parse.parser.TenaliSqlCommandLexer;
import com.qubole.tenali.parse.parser.config.CommandType;

import java.io.IOException;

/**
 * Created by devjyotip on 5/10/18.
 */
public class SqlCommandTestHelper {

  public static void parseHive(String command) throws IOException {
    TenaliSqlCommandHandler handler = new TenaliSqlCommandHandler(new TenaliSqlCommandLexer(),
            new TenaliHiveSqlParser());
    handler.submit(CommandType.Type.SQL, command);
  }

    public static void parseAnsiSql(String command) throws IOException {
        TenaliSqlCommandHandler handler = new TenaliSqlCommandHandler(new TenaliSqlCommandLexer(),
                new TenaliAnsiSqlParser());
        handler.submit(CommandType.Type.SQL, command);
    }
}
