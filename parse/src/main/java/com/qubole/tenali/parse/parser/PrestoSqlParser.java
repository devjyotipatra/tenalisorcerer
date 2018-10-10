package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.config.QueryType;
import org.apache.calcite.sql.SqlNode;

import java.io.IOException;

public class PrestoSqlParser extends AnsiSqlParser {

    @Override
    public ParseObject<SqlNode> parse(QueryType queryType, String command) throws IOException {
        ParseObject parseObject = new ParseObject(CommandType.PRESTO, queryType);

        ParseObject sqlParseObject = super.parse(queryType, command);
        parseObject.setParseObject(sqlParseObject.getParseObject());
        parseObject.setParseErrorMessage(sqlParseObject.getParseErrorMessage());

        return parseObject;
    }
}
