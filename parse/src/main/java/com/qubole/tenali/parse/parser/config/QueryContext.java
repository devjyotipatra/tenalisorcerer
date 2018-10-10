package com.qubole.tenali.parse.parser.config;

import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.parse.parser.TenaliParser;


public class QueryContext<T> {

    T ast;

    QueryType queryType = QueryType.UNKNOWN;

    TenaliAstNode root;

    public QueryContext(QueryType queryType) {
        this.queryType = queryType;
    }

    public QueryContext(TenaliParser.ParseObject parseObject) {
        queryType = parseObject.getQueryType();

        Object obj = parseObject.getParseObject();
        if(obj != null) {
            ast = (T) obj;
        }
    }

    public QueryContext(T ast) {
        this.ast = ast;
    }

    public QueryContext(QueryType queryType, T ast) {
        this.queryType = queryType;
        this.ast = ast;
    }

    public void setQueryType(QueryType type) {
        this.queryType = queryType;
    }

    public QueryType getQueryType() {
        return queryType;
    }


    public T getAst() {
        return ast;
    }


    public TenaliAstNode getTenaliAst() {
        return root;
    }

    public void setAstRoot(TenaliAstNode root) {
        this.root = root;
    }

    public String convertAstToString() {
        return ast.toString();
    }

}
