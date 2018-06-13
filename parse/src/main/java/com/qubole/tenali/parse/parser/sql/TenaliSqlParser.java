package com.qubole.tenali.parse.parser.sql;


import antlr4.QDSCommandLexer;
import com.qubole.tenali.parse.Parsers;
import com.qubole.tenali.parse.QueryContext;
import com.qubole.tenali.parse.parser.TenaliParser;
import com.qubole.tenali.parse.exception.CommandErrorListener;
import com.qubole.tenali.parse.exception.CommandParseError;
import com.qubole.tenali.parse.exception.SQLSyntaxError;

import antlr4.QDSCommandParser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by devjyotip on 5/29/18.
 */
public class TenaliSqlParser implements TenaliParser {

  final SqlParser.Config parserConfig;

  ParseDriver parseDriver = new ParseDriver();

  public TenaliSqlParser() {
      /*parserConfig = SqlParser.configBuilder()
              .setUnquotedCasing(Casing.UNCHANGED)
              .setQuotedCasing(Casing.UNCHANGED)
              .setQuoting(Quoting.DOUBLE_QUOTE)
              .setConformance(TenaliConformance.instance())
              .build();*/

    parserConfig = SqlParser.configBuilder()
            .setLex(Lex.MYSQL_ANSI)
            .setUnquotedCasing(Casing.UNCHANGED)
            .setQuotedCasing(Casing.UNCHANGED)
            .setQuoting(Quoting.DOUBLE_QUOTE)
            .setConformance(SqlConformanceEnum.MYSQL_5)
            .build();
  }

  public void parse(String command, QueryType qt) throws IOException {
    InputStream antlrInputStream =
        new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));

    QDSCommandLexer lexer =
        new QDSCommandLexer(CharStreams.fromStream(antlrInputStream, StandardCharsets.UTF_8));

    QDSCommandParser parser = new QDSCommandParser(new CommonTokenStream(lexer));
    parser.setBuildParseTree(true);
    parser.removeErrorListeners();
    parser.addErrorListener(new CommandErrorListener());

    ParseTree tree;
    try {
      tree = parser.parse();
    } catch (CommandParseError e) {
      throw new SQLSyntaxError(e);
    }


    System.out.println("---- command parsing ----");

    Parsers.CommandParser commandParser = new Parsers.CommandParser();
    commandParser.visit(tree);

    QueryContext rootCtx = commandParser.getQCRoot();

    System.out.println("---- sql parsing ----" + qt.toString());
    switch(qt) {
      case HIVE:
      case SPARK_SQL:
        parseWithHive(rootCtx);
        break;

      case ANSI_SQL:
        parseWithCalcite(rootCtx);
    }
  }



  public void parseWithCalcite(QueryContext qctx) throws IOException {
    while(qctx != null) {
      String sql = qctx.getQueryStmt();
      try {
        SqlParser parser = SqlParser.create(sql, parserConfig);

        SqlNode ast = parser.parseStmt();

        if(ast != null) {
          SqlAstPrintVisitor printer = new SqlAstPrintVisitor();
          ast.accept(printer);
          System.out.println(printer.toString());

          qctx.setQueryAst(ast);
        }
      } catch (SqlParseException e) {
        /*UserException.Builder builder = UserException
            .parseError(e)
            .addContext("SQL Query", formatSQLParsingError(sql, e.getPos()));
        if (isInnerQuery) {
          builder.message("Failure parsing a view your query is dependent upon.");
        }
        throw builder.build(logger);*/
        throw new IOException(e);
      }

      qctx = qctx.getChild();
    }
  }


  public void parseWithHive(QueryContext qctx) throws IOException {
    while(qctx != null) {
      String sql = qctx.getQueryStmt();
      try {
        ASTNode astNode = parseDriver.parse(sql);
        System.out.println(astNode.dump());
      } catch (Exception e) {
        throw new IOException("Parse failed for: " + sql + " Exception: " +  e.getMessage());
      }

      qctx = qctx.getChild();
    }
  }

}
