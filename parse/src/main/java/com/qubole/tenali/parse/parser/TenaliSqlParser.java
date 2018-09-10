package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandType;

import java.util.HashMap;
import java.util.Map;

public final class TenaliSqlParser {

    private static Map<CommandType, TenaliParser> sqlParserMap = new HashMap<>();


    private TenaliSqlParser() {
        throw new AssertionError("Final class; cannot instantiate");
    }


    private static synchronized TenaliParser getParserInstance(CommandType commandType) {
        TenaliParser parser = sqlParserMap.get(commandType);

        if(parser == null) {
            switch(commandType.getType()) {
                case HIVE:
                    System.out.println("Creating Hive Parser ");
                    parser = new TenaliHiveSqlParser();
                    break;
                case SQL:
                case PRESTO:
                    System.out.println("Creating Ansi Sql Parser ");
                    parser = new TenaliAnsiSqlParser();
                    break;
            }

            sqlParserMap.put(commandType, parser);
        }

        return parser;
    }

}
