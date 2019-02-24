package com.qubole.tenali.parse.config;

import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;


public class QueryContext {

    QueryType queryType = QueryType.UNKNOWN;

    Object parseAst;

    TenaliAstNode root;


    public QueryContext(QueryType queryType) {
        this.queryType = queryType;
    }

    public QueryContext(QueryType queryType, Object parseAst) {
        this.queryType = queryType;
        this.parseAst = parseAst;
    }



   /* public QueryContext(TenaliParser.ParseObject parseObject) {
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
    }*/


    public QueryType getQueryType() {
        return queryType;
    }


    public void setTenaliAst(TenaliAstNode ast) {
        root = ast;
    }

    public TenaliAstNode getTenaliAst() {
        return root;
    }

    public Object getParseAst() {
        return parseAst;
    }

    public void setParseAst(Object parseAst) {
        this.parseAst = parseAst;
    }
}
