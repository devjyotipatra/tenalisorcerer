package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.QueryType;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.io.IOException;

import static org.apache.calcite.sql.parser.SqlParser.configBuilder;

public class TenaliBabelParser extends AnsiSqlParser {

    final Planner planner;

    public TenaliBabelParser() {
        SqlParser.Config parserConfig = configBuilder()
                .setConformance(SqlConformanceEnum.BABEL)
                .setParserFactory(SqlBabelParserImpl.FACTORY).build();

        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .parserConfig(parserConfig).build();

        planner = Frameworks.getPlanner(frameworkConfig);
    }

    @Override
    public ParseObject<SqlNode> parse(QueryType queryType, String command) throws IOException {
        ParseObject parseObject = new ParseObject(CommandType.PRESTO, queryType);

        try {
            parseObject.setParseObject(planner.parse(command));
        } catch (SqlParseException ex) {
            parseObject.setParseErrorMessage("Parsing Error From Babel parser  " + ex.getMessage());
        }

        return parseObject;
    }
}
