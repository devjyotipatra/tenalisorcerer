package com.qubole.tenali.parse;

import antlr4.QDSCommandParser;
import org.apache.calcite.sql.SqlNode;


/**
 * Created by devjyotip on 5/30/18.
 */
public class QueryContext implements Cloneable {

  QueryType queryType;

  String queryStmt;

  SqlNode queryAst;

  QueryContext parent;

  QueryContext child;

  public QueryContext() {
    this(QueryType.UNKNOWN, null);
  }

  public QueryContext(QueryType queryType) {
    this(queryType, null);
  }

  public QueryContext(QueryType queryType, QueryContext parent) {
    this.queryType = queryType;
    this.parent = parent;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public QueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(QueryType queryType) {
    this.queryType = queryType;
  }

  public QueryContext getParent() {
    return parent;
  }

  public void setParent(QueryContext parent) {
    this.parent = parent;
  }

  public QueryContext getChild() {
    return child;
  }

  public void setChild(QueryContext child) {
    this.child = child;
  }

  public String getQueryStmt() {
    return queryStmt;
  }

  public void setQueryStmt(String queryStmt) {
    this.queryStmt = queryStmt;
  }

  public SqlNode getQueryAst() {
    return queryAst;
  }

  public void setQueryAst(SqlNode queryAst) {
    this.queryAst = queryAst;
  }

  public void addChild(QueryContext qctx) {
    this.setChild(qctx);
    qctx.setParent(this);
  }


  public enum QueryType  {
    UNKNOWN(Integer.MIN_VALUE),
    SET(QDSCommandParser.Q_SET),
    ADD_JAR(QDSCommandParser.Q_ADD_JAR),
    USE(QDSCommandParser.Q_USE),
    CTE(QDSCommandParser.Q_CTE),
    CREATE_FUNCTION(QDSCommandParser.Q_CREATE_FUNCTION),
    INSERT_INTO(QDSCommandParser.Q_INSERT_INTO),
    INSERT_OVERWRITE(QDSCommandParser.Q_INSERT_OVERWRITE),
    SELECT(QDSCommandParser.Q_SELECT),
    DROP_TABLE(QDSCommandParser.Q_DROP_TABLE),
    DROP_VIEW(QDSCommandParser.Q_DROP_VIEW),
    ALTER_TABLE(QDSCommandParser.Q_ALTER_TABLE),
    CREATE_TABLE(QDSCommandParser.Q_CREATE_TABLE),
    CREATE_EXTERNAL_TABLE(QDSCommandParser.Q_CREATE_EXTERNAL_TABLE),
    CTAS(QDSCommandParser.Q_CTAS),
    CREATE_VIEW(QDSCommandParser.Q_CREATE_VIEW);

    int queryType;

    QueryType(int queryType) {
      this.queryType = queryType;
    }

    public int getQueryType() {
      return queryType;
    }
  }
}
