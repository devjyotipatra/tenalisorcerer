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

    static ParseDriver parseDriver;

    public HiveSqlParser() {}

    public void prepare() {
        //conf = new HiveConf();
        //conf.setBoolVar(HiveConf.ConfVars.HIVE_SUPPORT_SQL11_RESERVED_KEYWORDS, false);
        /*String scratchDir = HiveConf.getVar(conf, HiveConf.ConfVars.SCRATCHDIR);
        conf.set("_hive.hdfs.session.path", scratchDir);
        conf.set("_hive.local.session.path", HiveConf.getVar(conf, HiveConf.ConfVars.LOCALSCRATCHDIR)
                + "/" + System.getProperty("user.name") + "/" + "000");*/

        parseDriver = new ParseDriver();
    }

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
                String message = String.format("Parse failed for: %s  \n Exception: ", sql, e.getMessage());
                LOG.error(message);
                e.printStackTrace();
                throw new TenaliSQLParseException(e);
            }
        }

        return parseObj;
    }

}