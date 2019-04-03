package com.qubole.tenali.parse.sql;

import com.qubole.tenali.parse.TenaliParser;
import com.qubole.tenali.parse.sql.datamodel.MetaNode;
import com.qubole.tenali.parse.sql.datamodel.SetNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class TenaliSqlParser implements TenaliParser {

    static final Pattern setPattern = Pattern.compile("set ([\\.\\d\\w-_]+)=([\\.\\w\\d-_]+)");

    public MetaNode parseUseStmt(String sql) {
        String[] tokens = sql.split("[\\s]+");
        return new MetaNode(tokens[1]);
    }


    public SetNode parseSetStmt(String sql) {
        SetNode node = null;
        Matcher m = setPattern.matcher(sql);

        if(m.find()) {
            node = new SetNode(m.group(2), m.group(2));
        }

        return node;
    }

}
