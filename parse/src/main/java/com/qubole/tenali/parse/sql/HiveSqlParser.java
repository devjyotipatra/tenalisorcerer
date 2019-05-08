package com.qubole.tenali.parse.sql;


import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import com.qubole.tenali.parse.sql.datamodel.MetaNode;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HiveSqlParser extends TenaliSqlParser {
    private static final Logger LOG = LoggerFactory.getLogger(HiveSqlParser.class);

    static volatile ParseDriver parseDriver;

    public HiveSqlParser() {
        if(parseDriver == null) {
            synchronized (this) {
                if(parseDriver == null) {
                    parseDriver = new ParseDriver();
                }
            }
        }
    }

    public void prepare() { }

    public QueryContext parse(QueryType queryType, String sql) throws TenaliSQLParseException {
        QueryContext parseObj = new QueryContext();

        if(queryType == QueryType.USE) {
            MetaNode node = (MetaNode) parseDdlStatement(sql, queryType);
            parseObj.setDefaultDB(node.statement);
            parseObj.setParseAst(node);
        } else if(queryType == QueryType.SET
                || queryType == QueryType.ALTER_TABLE
                || queryType == QueryType.ADD_JAR
                || queryType == QueryType.CREATE_DATABASE
                || queryType == QueryType.UNKNOWN) {
            parseObj.setParseAst(parseDdlStatement(sql, queryType));
        } else {
            try {
                ASTNode root = parseDriver.parse(sql);

                if (root != null) {
                    parseObj.setParseAst(root.getChild(0));
                }
            } catch (Exception e) {
                String message = String.format("Parse failed for:   %s  \n Exception: ", sql, e.getMessage());
                e.printStackTrace();
                throw new TenaliSQLParseException(message, e);
            }
        }

        return parseObj;
    }

}