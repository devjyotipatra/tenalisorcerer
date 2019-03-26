package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.sql.HiveSqlParser;
import com.qubole.tenali.parse.sql.PrestoSqlParser;

import java.util.HashMap;
import java.util.Map;

// This is experimental
public final class SqlParser {

    private static Map<CommandType, TenaliParser> sqlParserMap = new HashMap<>();


    private SqlParser() {
        throw new AssertionError("Final class; cannot instantiate");
    }


    private static synchronized TenaliParser getParserInstance(CommandType commandType) {
        TenaliParser parser = sqlParserMap.get(commandType);

        if(parser == null) {
            switch(commandType) {
                case HIVE:
                    System.out.println("Creating Hive Parser ");
                    parser = new HiveSqlParser();
                    break;
                case SQL:
                case PRESTO:
                    System.out.println("Creating Ansi Sql Parser ");
                    parser = new PrestoSqlParser();
                    break;
            }

            sqlParserMap.put(commandType, parser);
        }

        return parser;
    }

}
