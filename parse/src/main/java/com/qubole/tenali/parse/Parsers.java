package com.qubole.tenali.parse;


import antlr4.QDSCommandBaseVisitor;
import com.qubole.tenali.parse.parser.TenaliParser;
import com.qubole.tenali.parse.parser.sql.TenaliSqlParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import com.qubole.tenali.parse.QueryContext.QueryType;

import static antlr4.QDSCommandLexer.*;

/**
 * Created by devjyotip on 5/18/18.
 */
public abstract class Parsers {

    final long queryId;

    private TenaliSqlParser sqlParser = null;

    public Parsers(long queryId) {
        this.queryId = queryId;
    }

    public TenaliParser createSqlParser() {
        if (sqlParser == null) {
            sqlParser = new TenaliSqlParser();
        }
        return sqlParser;
    }


    public static class CommandParser extends QDSCommandBaseVisitor<QueryContext> {
        /**
         * Function for segregating query types and calling the parser with the right context
         * <p>
         * Here we have called the same parser method (TenaliHiveParser.parse) for all the query types.
         * This can be changed in future to call specific parsers like
         * TenaliHiveSelectParser, TenaliHiveInsertParser, TenaliHiveCreateParser
         * This would help in avoiding lot of (if-else) wiring inside the Parse function and result in
         * cleaner code.
         *
         * @param ctx: Sql statement context from Antlr
         */

        QueryContext root = null;

        QueryContext currentQueryContext = null;


        @Override
        public QueryContext visitParse(antlr4.QDSCommandParser.ParseContext ctx) {
            System.out.println("------ visit parse -----");
            if (ctx.getChild(0) == ctx.EOF()) {
                return null;
            }

            root = new QueryContext(QueryContext.QueryType.UNKNOWN);
            currentQueryContext = root;
            return visitChildren(ctx);
        }


        @Override
        public QueryContext visitSql_stmt(antlr4.QDSCommandParser.Sql_stmtContext ctx) {
            String stmt = ctx.getText().trim();
            int queryType = ctx.op.getType();

            QueryContext qctx = null;
            if (currentQueryContext.queryType == QueryType.UNKNOWN) {
                qctx = currentQueryContext;
            } else {
                qctx = new QueryContext();
            }

            qctx.setQueryStmt(stmt);

            System.out.println(" <=  visitSql_stmt  => " + stmt);

            switch (queryType) {
                case Q_SELECT:
                    qctx.setQueryType(QueryType.SELECT);
                    break;
                case Q_INSERT_INTO:
                    qctx.setQueryType(QueryType.INSERT_INTO);
                    break;
                case Q_INSERT_OVERWRITE:
                    qctx.setQueryType(QueryType.INSERT_OVERWRITE);
                    break;
                case Q_CTAS:
                    qctx.setQueryType(QueryType.CTAS);
                    break;
                case Q_CREATE_VIEW:
                    qctx.setQueryType(QueryType.CREATE_VIEW);
                    break;
                case Q_CTE:
                    qctx.setQueryType(QueryType.CTE);
                    break;
                case Q_CREATE_TABLE:
                    qctx.setQueryType(QueryType.CREATE_TABLE);
                case Q_CREATE_EXTERNAL_TABLE:
                    qctx.setQueryType(QueryType.CREATE_EXTERNAL_TABLE);
                    break;
                case Q_DROP_TABLE:
                    qctx.setQueryType(QueryType.DROP_TABLE);
                case Q_DROP_VIEW:
                    qctx.setQueryType(QueryType.DROP_VIEW);
                    break;
                case Q_USE:
                    qctx.setQueryType(QueryType.USE);
                    //qctx.setDefaultSchema(getDefaultSchema(stmt));
                    break;
                case Q_CREATE_FUNCTION:
                    qctx.setQueryType(QueryType.CREATE_FUNCTION);
                    //cctx.setIsTemporaryFunctionUsed(true);
                    //cctx.addTemporaryFunction(getFunction(stmt));
                    break;
                case Q_SET:
                    qctx.setQueryType(QueryType.SET);
                    break;
                case Q_ALTER_TABLE:
                    qctx.setQueryType(QueryType.ALTER_TABLE);
                    break;
                case Q_ADD_JAR:
                    qctx.setQueryType(QueryType.ADD_JAR);
                    //cctx.setIsExternalJarUsed(true);
                    //cctx.addJarPath(getJar(stmt));
                    break;
            }

            //cctx.addQueryContext(aggregateResult(defaultResult(), qctx));
            return qctx;
        }


        @Override
        public QueryContext visitChildren(RuleNode node) {
            int n = node.getChildCount();

            for (int i = 0; i < n && this.shouldVisitNextChild(node, currentQueryContext); ++i) {
                ParseTree c = node.getChild(i);
                System.out.println(i + " <=  visitChildren  => " + c.getText());
                QueryContext result = c.accept(this);

                if (result != null && result != currentQueryContext) {
                    currentQueryContext = this.aggregateResult(currentQueryContext, result);
                }
            }

            return null;
        }


        @Override
        public boolean shouldVisitNextChild(RuleNode node, QueryContext currentResult) {
            if (currentResult.getQueryType() == QueryType.UNKNOWN) {
                return true;
            }
            return true;
        }


        @Override
        public QueryContext aggregateResult(QueryContext prevResult,
                                            QueryContext childResult) {
            System.out.println("adding child ");
            prevResult.addChild(childResult);
            return childResult;
        }


        public QueryContext getQCRoot() {
            return root;
        }
    }

}

