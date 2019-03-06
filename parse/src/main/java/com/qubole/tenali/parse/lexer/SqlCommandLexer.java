package com.qubole.tenali.parse.lexer;

import antlr4.QDSCommandBaseVisitor;
import antlr4.QDSCommandLexer;
import antlr4.QDSCommandParser;
import com.qubole.tenali.parse.AbstractCommandHandler;
import com.qubole.tenali.parse.TenaliLexer;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.CommandErrorListener;
import com.qubole.tenali.parse.exception.CommandParseError;
import com.qubole.tenali.parse.exception.SQLSyntaxError;
import com.qubole.tenali.parse.config.CommandContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static antlr4.QDSCommandLexer.*;


public class SqlCommandLexer extends QDSCommandBaseVisitor<CommandContext> implements TenaliLexer<CommandContext> {
    /**
     * Function for segregating query types and calling the parser with the right context
     * <p>
     * Here we have called the same parser method (HiveSqlParser.parse) for all the query types.
     * This can be changed in future to call specific parsers like
     * TenaliHiveSelectParser, TenaliHiveInsertParser, TenaliHiveCreateParser
     * This would help in avoiding lot of (if-else) wiring inside the Parse function and result in
     * cleaner code.
     *
     * @param ctx: Sql statement context from Antlr
     */

    private static final Logger LOG = LoggerFactory.getLogger(SqlCommandLexer.class);

    CommandType commandType;

    CommandContext root;


    public SqlCommandLexer() {
        this(CommandType.SQL);
    }

    public SqlCommandLexer(CommandType commandType) {
        this.commandType = commandType;
    }


    @Override
    public CommandContext visitParse(antlr4.QDSCommandParser.ParseContext ctx) {
        if (ctx.getChild(0) == ctx.EOF()) {
            return null;
        }

        return visitChildren(ctx);
    }


    @Override
    public CommandContext visitSql_stmt(antlr4.QDSCommandParser.Sql_stmtContext ctx) {
        CommandContext qctx = new CommandContext();

        String stmt = ctx.getText().trim();
        int queryType = ctx.op.getType();

        qctx.setStmt(stmt);

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
            case Q_CREATE_VIEW:
                qctx.setQueryType(QueryType.CREATE_VIEW);
                break;
            case Q_CTE:
                qctx.setQueryType(QueryType.CTE);
                break;
            case Q_CREATE_TABLE:
            case Q_CREATE_EXTERNAL_TABLE:
                qctx.setQueryType(QueryType.CREATE_TABLE);
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
            default:
                qctx.setIsDDLQuery();
                if(isSupportedDDL(queryType)) {
                    qctx.setIsSupportedDDLQuery();
                }
        }

        return qctx;
    }


    @Override
    public CommandContext visitChildren(RuleNode node) {
        CommandContext cctx = null;

        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, root); ++i) {
            ParseTree c = node.getChild(i);
            CommandContext result = c.accept(this);

            if(result != null && result.getQueryType() != QueryType.UNKNOWN) {
                if(root == null) {
                    result.setAsRootNode();
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


    @Override
    public void extract(String command) {
        try {
            InputStream antlrInputStream =
                    new ByteArrayInputStream(command.trim().getBytes(StandardCharsets.UTF_8));

            QDSCommandLexer lexer =
                    new QDSCommandLexer(CharStreams.fromStream(antlrInputStream, StandardCharsets.UTF_8));

            QDSCommandParser parser = new QDSCommandParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(true);
            parser.removeErrorListeners();
            parser.addErrorListener(new CommandErrorListener());

            ParseTree tree = parser.parse();
            visit(tree);

        } catch (CommandParseError e) {
            throw new SQLSyntaxError(e);
        } catch (IOException io) {

        }
    }


    @Override
    public CommandContext getRootContext() {
        return root;
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }


    public boolean isSupportedDDL(int type) {
        return type == Q_CREATE_TABLE
                || type == Q_CREATE_VIEW
                || type == Q_DROP_VIEW;
    }

}
