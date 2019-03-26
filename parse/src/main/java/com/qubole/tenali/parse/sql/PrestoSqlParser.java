package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.TenaliParser;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.config.TenaliConformance;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.validate.SqlConformance;

import java.io.IOException;

public class PrestoSqlParser implements TenaliParser {

    SqlParser sqlParser = null;

    @Override
    public QueryContext parse(QueryType queryType, String sql) throws IOException {
        QueryContext parseObj = new QueryContext();

        if(queryType == QueryType.USE) {
            String[] tokens = sql.split("[\\s]+");
            parseObj.setDefaultDB(tokens[1]);
        } else {
            parseObj.setParseAst(parse(sql));
        }

        return parseObj;
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

        Quoting quoting = Quoting.DOUBLE_QUOTE;
        Casing unquotedCasing = Casing.TO_UPPER;
        Casing quotedCasing = Casing.UNCHANGED;
        SqlConformance conformance = TenaliConformance.instance();

        sqlParser = SqlParser.create("",
                SqlParser.configBuilder()
                        .setParserFactory(SqlParserImpl.FACTORY)
                        .setQuoting(quoting)
                        .setUnquotedCasing(unquotedCasing)
                        .setQuotedCasing(quotedCasing)
                        .setConformance(conformance)
                        .build());
    }

    public SqlNode parse(String command) throws IOException {
        SqlNode ast = null;

        try {
            ast = sqlParser.parseQuery(command);
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

        return ast;
    }
}
