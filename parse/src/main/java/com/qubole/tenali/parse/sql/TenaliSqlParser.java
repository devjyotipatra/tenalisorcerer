package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.TenaliParser;
import com.qubole.tenali.parse.sql.datamodel.MetaNode;
import com.qubole.tenali.parse.sql.datamodel.SetNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class TenaliSqlParser implements TenaliParser {

    static final Pattern setPattern = Pattern.compile("set\\s+([\\.\\d\\w-_]+)\\s*=\\s*([\\.\\w\\d-_]+)");

    public MetaNode parseUseStmt(String sql) {
        String[] tokens = sql.split("[\\s]+");
        return new MetaNode("USE", tokens[1]);
    }


    public SetNode parseSetStmt(String sql) {
        SetNode node = null;
        Matcher m = setPattern.matcher(sql.toLowerCase());

        if(m.find()) {
            node = new SetNode(m.group(1), m.group(2));
        }

        return node;
    }

}
