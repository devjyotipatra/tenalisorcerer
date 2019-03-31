package com.qubole.tenali.parse.sql;


import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HiveSqlParser extends TenaliSqlParser {
    private static final Logger LOG = LoggerFactory.getLogger(HiveSqlParser.class);

    static ParseDriver parseDriver;

    public HiveSqlParser() {}

    public void prepare() {
        parseDriver = new ParseDriver();
    }

    public QueryContext parse(QueryType queryType, String sql) throws TenaliSQLParseException {
        QueryContext parseObj = new QueryContext();

        if(queryType == QueryType.USE) {
            parseObj.setDefaultDB(parseUseStmt(sql));
        } else {
            try {
                ASTNode root = parseDriver.parse(sql);

                if (root != null) {
                    parseObj.setParseAst(root.getChild(0));
                }
            } catch (Exception e) {
                String message = String.format("Parse failed for: %s  \n Exception: ", sql, e.getMessage());
                LOG.error(message);
                throw new TenaliSQLParseException(e);
            }
        }

        return parseObj;
    }

}