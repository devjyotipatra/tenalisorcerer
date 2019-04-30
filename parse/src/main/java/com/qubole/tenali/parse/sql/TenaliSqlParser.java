package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.TenaliParser;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.sql.datamodel.MetaNode;
import com.qubole.tenali.parse.sql.datamodel.SetNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class TenaliSqlParser implements TenaliParser {

    static final Pattern setPattern = Pattern.compile("set\\s+([\\.\\d\\w-_]+)\\s*=\\s*([\\.\\w\\d-_\\s]+)");


    public TenaliAstNode parseDdlStatement(String sql, QueryType queryType) {
        if(queryType == QueryType.USE) {
            String[] tokens = sql.split("[\\s]+");
            return new MetaNode("USE", tokens[1]);
        }

        if(queryType == QueryType.CREATE_DATABASE) {
            return new MetaNode("CREATEDB", sql);
        }

        if(queryType == QueryType.ALTER_TABLE) {
            return new MetaNode("ALTER", sql);
        }

        if(queryType == QueryType.ADD_JAR) {
            return new MetaNode("ADDJAR", sql);
        }

        if(queryType == QueryType.SET) {
            SetNode node = null;
            Matcher m = setPattern.matcher(sql.toLowerCase());

            if(m.find()) {
                node = new SetNode(m.group(1), m.group(2));
            }
            return node;
        }

        return new MetaNode("UNKNOWN", sql);
    }

}
