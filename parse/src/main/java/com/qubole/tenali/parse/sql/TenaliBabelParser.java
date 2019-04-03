package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import static org.apache.calcite.sql.parser.SqlParser.configBuilder;

public class TenaliBabelParser extends TenaliSqlParser {

    private static final Logger LOG = LoggerFactory.getLogger(TenaliBabelParser.class);

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
    public QueryContext parse(QueryType queryType, String sql) throws TenaliSQLParseException {
        QueryContext parseObject = new QueryContext();

        try {
            parseObject.setParseAst(planner.parse(sql));
        } catch (SqlParseException e) {
            String message = String.format("Parse failed for: %s  \n Exception: ", sql, e.getMessage());
            LOG.error(message);
            throw new TenaliSQLParseException(e);
        }

        return parseObject;
    }

    public void prepare() {}
}
