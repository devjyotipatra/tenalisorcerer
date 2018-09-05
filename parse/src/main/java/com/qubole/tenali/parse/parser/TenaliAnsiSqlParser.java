package com.qubole.tenali.parse.parser;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubole.tenali.parse.parser.sql.CalciteAstToBaseAstConverter;
import com.qubole.tenali.parse.parser.sql.TenaliBaseAstAliasResolver;
import com.qubole.tenali.parse.parser.sql.datamodel.BaseAstNode;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

import java.io.IOException;

public class TenaliAnsiSqlParser implements TenaliParser {

    final SqlParser.Config parserConfig;

    public TenaliAnsiSqlParser() {
        /*parserConfig = SqlParser.configBuilder()
              .setUnquotedCasing(Casing.UNCHANGED)
              .setQuotedCasing(Casing.UNCHANGED)
              .setQuoting(Quoting.DOUBLE_QUOTE)
              .setConformance(TenaliConformance.instance())
              .build();*/

        parserConfig = SqlParser.configBuilder()
                .setLex(Lex.MYSQL)
                .setUnquotedCasing(Casing.UNCHANGED)
                .setQuotedCasing(Casing.UNCHANGED)
                .setQuoting(Quoting.DOUBLE_QUOTE)
                //.setConformance(SqlConformanceEnum.MYSQL_5)
                //.setConformance(TenaliConformance.instance())
                .build();
    }

    public void parse(String sql) throws IOException {
        SqlParser parser = SqlParser.create(sql, parserConfig);

        try {

            SqlNode ast = parser.parseStmt();

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
    }
}
