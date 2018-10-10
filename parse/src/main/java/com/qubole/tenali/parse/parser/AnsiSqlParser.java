package com.qubole.tenali.parse.parser;


import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.config.QueryType;
import com.qubole.tenali.parse.parser.config.TenaliConformance;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

import java.io.IOException;

public class AnsiSqlParser implements TenaliParser {

    static SqlParser.Config parserConfig;

    public AnsiSqlParser() {
        /*parserConfig = SqlParser.configBuilder()
              .setUnquotedCasing(Casing.UNCHANGED)
              .setQuotedCasing(Casing.UNCHANGED)
              .setQuoting(Quoting.DOUBLE_QUOTE)
              .setConformance(TenaliConformance.instance())
              .build();*/


    }

    public void prepare() {
        /*parserConfig = SqlParser.configBuilder()
                .setLex(Lex.MYSQL)
                .setUnquotedCasing(Casing.UNCHANGED)
                .setQuotedCasing(Casing.UNCHANGED)
                .setQuoting(Quoting.DOUBLE_QUOTE)
                //.setConformance(SqlConformanceEnum.MYSQL_5)
                //.setConformance(TenaliConformance.instance())
                .build();*/

        parserConfig = SqlParser.configBuilder()
                .setUnquotedCasing(Casing.UNCHANGED)
                .setQuotedCasing(Casing.UNCHANGED)
                .setQuoting(Quoting.DOUBLE_QUOTE)
                .setConformance(TenaliConformance.instance())
                .build();
    }

    public ParseObject<SqlNode> parse(QueryType queryType, String command) throws IOException {
        ParseObject parseobject = new ParseObject(CommandType.SQL, queryType);
        SqlNode ast = null;

        SqlParser parser = SqlParser.create(command, parserConfig);

        try {
                ast = parser.parseStmt();
                parseobject.setParseObject(ast);
            /*if (ast != null) {
                CalciteAstToBaseAstConverter converter = new CalciteAstToBaseAstConverter();
                BaseAstNode root = ast.accept(converter);

                String jsonAst = converter.convertToString(root);
                System.out.println(jsonAst);

                BaseAstNode cRoot = new ObjectMapper().readValue(jsonAst, BaseAstNode.class);

                TenaliBaseAstAliasResolver visitor = new TenaliBaseAstAliasResolver();
                visitor.dfsFindAlias(cRoot);

                jsonAst = converter.convertToString(cRoot);
                System.out.println(jsonAst);

            }*/
        } catch (SqlParseException e) {
        /*UserException.Builder builder = UserException
            .parseError(e)
            .addContext("SQL Query", formatSQLParsingError(sql, e.getPos()));
        if (isInnerQuery) {
          builder.message("Failure parsing a view your query is dependent upon.");
        }
        throw builder.build(logger);*/
            throw new IOException(e);
        } //catch (JsonProcessingException jpe) {
        /*UserException.Builder builder = UserException
            .parseError(e)
            .addContext("SQL Query", formatSQLParsingError(sql, e.getPos()));
        if (isInnerQuery) {
          builder.message("Failure parsing a view your query is dependent upon.");
        }
        throw builder.build(logger);*/
        //    throw new IOException(jpe);
       // }

        return parseobject;
    }
}
