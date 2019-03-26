package com.qubole.tenali.util;

import com.qubole.tenali.parse.SqlCommandHandler;
import com.qubole.tenali.parse.catalog.Catalog;
import com.qubole.tenali.parse.catalog.CatalogResolver;
import com.qubole.tenali.parse.sql.*;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.lexer.HiveCommandLexer;
import com.qubole.tenali.parse.lexer.PrestoCommandLexer;
import com.qubole.tenali.parse.lexer.SqlCommandLexer;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.sql.visitor.TenaliAstAliasResolver;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.parse.util.CachingMetastore;
import org.apache.calcite.sql.SqlNode;

import java.io.IOException;

/**
 * Created by devjyotip on 5/10/18.
 */
public class SqlCommandTestHelper {

    public static String transformHiveAst(String command) {
        Catalog catalog = null;
        try {
            catalog = catalog = new CachingMetastore(5911, "api.qubole.com",
                    "xxxx",
                    "mojave-redis.9qcbtf.0001.use1.cache.amazonaws.com");
            return transformHiveAst(command, catalog);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String transformHiveAst(String command, int accountId, String env,
                                          String authToken, String cachingServerUrl) throws IOException {
        try {
            Catalog catalog = new CachingMetastore(accountId, env, authToken, cachingServerUrl);
            return transformHiveAst(command, catalog);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String transformHiveAst(String command, Catalog catalog) throws IOException {
        CommandContext ctx = null;
        try {
            ctx = new SqlCommandHandler(CommandType.HIVE)
                    .setLexer(new HiveCommandLexer())
                    .setParser(new HiveSqlParser())
                    .setTransformer(new HiveAstTransformer())
                    .setTransformer(new TenaliAstAliasResolver(new CatalogResolver(catalog)))
                    .build(command);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return ctx.getStmt();
    }

    public static String parseHive(String command) throws IOException {
        CommandContext ctx = null;
        try {
            Catalog catalog = new CachingMetastore(5911, "api.qubole.com",
                    "xxxx",
                    "mojave-redis.9qcbtf.0001.use1.cache.amazonaws.com");
            ctx =  new SqlCommandHandler(CommandType.HIVE)
                    .setLexer(new HiveCommandLexer())
                    .setParser(new HiveSqlParser())
                    .setTransformer(new HiveAstTransformer())
                    .setTransformer(new TenaliAstAliasResolver(new CatalogResolver(catalog)))
                    .build(command);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return ctx.getStmt();
    }

    public static TenaliAstNode transformPrestoQuery(String command) throws IOException {
        CommandContext ctx  =  new SqlCommandHandler(CommandType.PRESTO)
                .setLexer(new PrestoCommandLexer())
                .setParser(new PrestoSqlParser())
                .setTransformer(new CalciteAstTransformer())
                //.setTransformer(new TenaliAstAliasResolver())
                .build(command);

        return ctx.getQueryContext().getTenaliAst();
    }


    public static SqlNode parsePrestoQuery(String command) throws IOException {
        CommandContext ctx  = new SqlCommandHandler(CommandType.PRESTO)
                .setLexer(new PrestoCommandLexer())
                .setParser(new TenaliBabelParser())
                .build(command);

        SqlNode ast = (SqlNode) ctx.getCurrentContext().getQueryContext().getParseAst();
        System.out.println(ast);
        return ast;
    }

    public static String parseSparkSql(String command) throws IOException {
        CommandContext ctx = new SqlCommandHandler(CommandType.SPARK_SQL)
                .setLexer(new SqlCommandLexer())
                .setParser(new HiveSqlParser())
                .build(command);

        return ctx.getStmt();
    }
}
