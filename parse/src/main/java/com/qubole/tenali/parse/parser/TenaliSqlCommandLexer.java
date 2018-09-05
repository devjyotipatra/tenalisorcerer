package com.qubole.tenali.parse.parser;

import antlr4.QDSCommandBaseVisitor;
import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.config.CommandContext;
import com.qubole.tenali.parse.parser.config.Query;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import static antlr4.QDSCommandLexer.*;


public class TenaliSqlCommandLexer extends QDSCommandBaseVisitor<CommandContext> implements TenaliLexer<CommandContext>  {
    /**
     * Function for segregating query types and calling the parser with the right context
     * <p>
     * Here we have called the same parser method (TenaliHiveSqlParser.parse) for all the query types.
     * This can be changed in future to call specific parsers like
     * TenaliHiveSelectParser, TenaliHiveInsertParser, TenaliHiveCreateParser
     * This would help in avoiding lot of (if-else) wiring inside the Parse function and result in
     * cleaner code.
     *
     * @param ctx: Sql statement context from Antlr
     */

    final CommandType.Type commandType = CommandType.Type.SQL;

    CommandContext root;


    @Override
    public CommandContext visitParse(antlr4.QDSCommandParser.ParseContext ctx) {
        System.out.println("------ visit parse -----");
        if (ctx.getChild(0) == ctx.EOF()) {
            return null;
        }

        return visitChildren(ctx);
    }


    @Override
    public CommandContext visitSql_stmt(antlr4.QDSCommandParser.Sql_stmtContext ctx) {
        String stmt = ctx.getText().trim();
        int queryType = ctx.op.getType();

        CommandContext qctx = new CommandContext();
        qctx.setStmt(stmt);

        System.out.println(" <=  visitSql_stmt  => " + stmt);

        switch (queryType) {
            case Q_SELECT:
                qctx.setType(new QueryType(Query.Type.SELECT));
                break;
            case Q_INSERT_INTO:
                qctx.setType(new QueryType(Query.Type.INSERT_INTO));
                break;
            case Q_INSERT_OVERWRITE:
                qctx.setType(new QueryType(Query.Type.INSERT_OVERWRITE));
                break;
            case Q_CTAS:
                qctx.setType(new QueryType(Query.Type.CTAS));
                break;
            case Q_CREATE_VIEW:
                qctx.setType(new QueryType(Query.Type.CREATE_VIEW));
                break;
            case Q_CTE:
                qctx.setType(new QueryType(Query.Type.CTE));
                break;
            case Q_CREATE_TABLE:
            case Q_CREATE_EXTERNAL_TABLE:
                qctx.setType(new QueryType(Query.Type.CREATE_TABLE));
                break;
            case Q_DROP_TABLE:
                qctx.setType(new QueryType(Query.Type.DROP_TABLE));
            case Q_DROP_VIEW:
                qctx.setType(new QueryType(Query.Type.DROP_VIEW));
                break;
            case Q_USE:
                qctx.setType(new QueryType(Query.Type.USE));
                //qctx.setDefaultSchema(getDefaultSchema(stmt));
                break;
            case Q_CREATE_FUNCTION:
                qctx.setType(new QueryType(Query.Type.CREATE_FUNCTION));
                //cctx.setIsTemporaryFunctionUsed(true);
                //cctx.addTemporaryFunction(getFunction(stmt));
                break;
            case Q_SET:
                qctx.setType(new QueryType(Query.Type.SET));
                break;
            case Q_ALTER_TABLE:
                qctx.setType(new QueryType(Query.Type.ALTER_TABLE));
                break;
            case Q_ADD_JAR:
                qctx.setType(new QueryType(Query.Type.ADD_JAR));
                //cctx.setIsExternalJarUsed(true);
                //cctx.addJarPath(getJar(stmt));
                break;
        }

        return qctx;
    }


    @Override
    public CommandContext visitChildren(RuleNode node) {
        CommandContext cctx = null;

        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, root); ++i) {
            ParseTree c = node.getChild(i);
            System.out.println(i + " <=  visitChildren  => " + c.getText());
            CommandContext result = c.accept(this);

            if(result != null && result.getType().getValue() != Query.Type.UNKNOWN) {
                if(root == null) {
                    root = result;
                    cctx = result;
                } else {
                    cctx = this.aggregateResult(root.getCurrentContext(), result);
                }
            }
        }

        return cctx;
    }

    @Override
    public CommandContext aggregateResult(CommandContext currentContext, CommandContext result) {
        currentContext.appendNewContext(result);
        return result;
    }


    public CommandContext getRootContext() {
        return root;
    }

    public CommandType.Type getLexerType() {
        return commandType;
    }

}
