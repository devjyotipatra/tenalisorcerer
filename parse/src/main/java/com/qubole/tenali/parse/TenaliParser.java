package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;


/**
 * Created by devjyotip on 5/28/18.
 */
public interface TenaliParser {

    QueryContext parse(QueryType queryType, String command) throws TenaliSQLParseException;

    void prepare();
}
