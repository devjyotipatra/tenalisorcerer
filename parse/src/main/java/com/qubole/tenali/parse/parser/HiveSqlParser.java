package com.qubole.tenali.parse.parser;


import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.config.QueryType;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.io.IOException;

public class HiveSqlParser implements TenaliParser {

    static ParseDriver parseDriver;

    public HiveSqlParser() {}

    public void prepare() {
        parseDriver = new ParseDriver();
    }

    public ParseObject<ASTNode> parse(QueryType queryType, String sql) throws IOException {
        ParseObject parseObj = new ParseObject(CommandType.HIVE, queryType);
        ASTNode ast = null;
        try {
            ASTNode root = parseDriver.parse(sql);

            if(root != null) {
                parseObj.setParseObject((ASTNode) root.getChild(0));
            }
        } catch (Exception e) {
            throw new IOException("Parse failed for: " + sql + " Exception: " +  e.getMessage());
        }

        return parseObj;
    }

}