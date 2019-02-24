package com.qubole.tenali.util;

import com.qubole.tenali.parse.AbstractCommandHandler;
import com.qubole.tenali.parse.sql.*;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.lexer.HiveCommandLexer;
import com.qubole.tenali.parse.lexer.PrestoCommandLexer;
import com.qubole.tenali.parse.lexer.SqlCommandLexer;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.sql.alias.TenaliAstAliasResolver;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import org.apache.calcite.sql.SqlNode;

import java.io.IOException;

/**
 * Created by devjyotip on 5/10/18.
 */
public class SqlCommandTestHelper {

    public static String transformHiveAst(String command) throws IOException {
        CommandContext ctx = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.HIVE)
                .setLexer(new HiveCommandLexer())
                .setParser(new HiveSqlParser())
                .setTransformer(new HiveAstTransformer())
                .setTransformer(new TenaliAstAliasResolver())
                .build(command);

        return ctx.getStmt();
    }

    public static String parseHive(String command) throws IOException {
        CommandContext ctx = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.HIVE)
                .setLexer(new HiveCommandLexer())
                .setParser(new HiveSqlParser())
                .setTransformer(new HiveAstTransformer())
                .setTransformer(new TenaliAstAliasResolver())
                .build(command);

        return ctx.getStmt();
    }

    public static TenaliAstNode transformPrestoQuery(String command) throws IOException {
        CommandContext ctx  = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.PRESTO)
                .setLexer(new PrestoCommandLexer())
                .setParser(new PrestoSqlParser())
                .setTransformer(new CalciteAstTransformer())
                .setTransformer(new TenaliAstAliasResolver())
                .build(command);

        return ctx.getQueryContext().getTenaliAst();
    }


    public static SqlNode parsePrestoQuery(String command) throws IOException {
        CommandContext ctx  = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.PRESTO)
                .setLexer(new PrestoCommandLexer())
                .setParser(new TenaliBabelParser())
                .build(command);

        SqlNode ast = (SqlNode) ctx.getCurrentContext().getQueryContext().getParseAst();
        System.out.println(ast);
        return ast;
    }

    public static String parseSparkSql(String command) throws IOException {
        CommandContext ctx = new AbstractCommandHandler
                .CommandParserBuilder(CommandType.SPARK_SQL)
                .setLexer(new SqlCommandLexer())
                .setParser(new HiveSqlParser())
                .build(command);

        return ctx.getStmt();
    }
}
