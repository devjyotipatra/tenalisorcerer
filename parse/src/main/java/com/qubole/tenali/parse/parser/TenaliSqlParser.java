package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandType;

import java.util.HashMap;
import java.util.Map;

public final class TenaliSqlParser {

    private static Map<CommandType, TenaliParser> sqlParserMap = new HashMap<>();

    private TenaliSqlParser() {
        throw new AssertionError("Final class; cannot instantiate");
    }


    public static void setParserInstance(Map<CommandType, TenaliParser> sqlParserMap) {
        for(Map.Entry<CommandType, TenaliParser> e : sqlParserMap.entrySet()) {
            setParserInstance(e.getKey(), e.getValue());
        }
    }

    public static synchronized void setParserInstance(CommandType commandType, TenaliParser parser) {
        if(parser != null && sqlParserMap.get(commandType) == null) {
            sqlParserMap.put(commandType, parser);
        }
    }

    public static synchronized TenaliParser getParserInstance(CommandType commandType) {
        TenaliParser parser = sqlParserMap.get(commandType);

        if(parser != null) {
            switch(commandType.getType()) {
                case HIVE:
                    System.out.println("Returning Hive Parser ");
                    break;
                case SQL:
                case PRESTO:
                    System.out.println("Returning Ansi Sql Parser ");
                    break;
            }
        }

        return parser;
    }
}
