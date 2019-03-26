package com.qubole.tenali.parse.config;

import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;


public class QueryContext {

    Object parseAst;

    TenaliAstNode root;

    String defaultDB = "default";

    String errorMessage;

    public QueryContext() {}

    public QueryContext(Object parseAst) {
        this.parseAst = parseAst;
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

    public String getDefaultDB() {
        return defaultDB;
    }

    public void setDefaultDB(String db) {
        defaultDB = db;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
