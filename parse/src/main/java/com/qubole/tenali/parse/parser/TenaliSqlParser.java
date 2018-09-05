package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.Command;

import java.util.HashMap;
import java.util.Map;

public final class TenaliSqlParser {

    private static Map<Command, TenaliParser> sqlParserMap = new HashMap<>();

    private TenaliSqlParser() {
        throw new AssertionError("Final class; cannot instantiate");
    }


    public static void setParserInstance(Map<Command, TenaliParser> sqlParserMap) {
        for(Map.Entry<Command, TenaliParser> e : sqlParserMap.entrySet()) {
            setParserInstance(e.getKey(), e.getValue());
        }
    }

    public static synchronized void setParserInstance(Command commandType, TenaliParser parser) {
        if(parser != null && sqlParserMap.get(commandType) == null) {
            sqlParserMap.put(commandType, parser);
        }
    }

    public static synchronized TenaliParser getParserInstance(Command commandType) {
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
