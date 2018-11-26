package com.qubole.tenali.util;

import com.qubole.tenali.parse.parser.AbstractCommandHandler;
import com.qubole.tenali.parse.parser.AnsiSqlParser;
import com.qubole.tenali.parse.parser.HiveSqlParser;
import com.qubole.tenali.parse.parser.PrestoSqlParser;
import com.qubole.tenali.parse.parser.lexer.HiveCommandLexer;
import com.qubole.tenali.parse.parser.lexer.PrestoCommandLexer;
import com.qubole.tenali.parse.parser.lexer.SqlCommandLexer;
import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.sql.CalciteAstTransformer;
import com.qubole.tenali.parse.parser.sql.HiveAstTransformer;

import java.io.IOException;

/**
 * Created by devjyotip on 5/10/18.
 */
public class SqlCommandTestHelper {

    public static String parseHive(String command) throws IOException {
        AbstractCommandHandler handler = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.HIVE)
                .setLexer(new HiveCommandLexer())
                .setParser(new HiveSqlParser())
                .setTransformer(new HiveAstTransformer())
                .build(command);

        return handler.getIthStatement(0);
    }

    public static String parsePresto(String command) throws IOException {
        AbstractCommandHandler handler = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.PRESTO)
                .setLexer(new PrestoCommandLexer())
                .setParser(new PrestoSqlParser())
                .setTransformer(new CalciteAstTransformer())
                .build(command);



        return handler.getIthStatement(0);
    }

    public static String parseSparkSql(String command) throws IOException {
        AbstractCommandHandler handler = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.SPARK_SQL)
                .setLexer(new SqlCommandLexer())
                .setParser(new HiveSqlParser())
                .build(command);

        return handler.getIthStatement(0);
    }
}
