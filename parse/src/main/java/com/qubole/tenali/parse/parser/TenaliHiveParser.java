package com.qubole.tenali.parse.parser;


import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.io.IOException;

public class TenaliHiveParser implements TenaliParser {

    static final ParseDriver parseDriver = new ParseDriver();

    public TenaliHiveParser() {}

    public void parse(String sql) throws IOException {
        try {
            ASTNode astNode = parseDriver.parse(sql);
            System.out.println(astNode.dump());
        } catch (Exception e) {
            throw new IOException("Parse failed for: " + sql + " Exception: " +  e.getMessage());
        }
    }

}