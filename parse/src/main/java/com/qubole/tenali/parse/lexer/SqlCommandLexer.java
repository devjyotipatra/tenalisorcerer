package com.qubole.tenali.parse.lexer;

import antlr4.QDSCommandBaseVisitor;
import antlr4.QDSCommandLexer;
import antlr4.QDSCommandParser;
import com.qubole.tenali.parse.TenaliLexer;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.CommandErrorListener;
import com.qubole.tenali.parse.exception.CommandParseError;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.exception.SQLSyntaxError;
import jline.internal.Log;
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

    CommandType commandType = CommandType.UNKNOWN;

    CommandContext root;

    CommandContext currentContext;


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

        String stmt = ctx.getText();
        qctx.setStmt(stmt);

        Log.info(String.format("lexing now  %s ", stmt));

        int queryType = ctx.op.getType();
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
                break;
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
                qctx.setQueryType(QueryType.UNKNOWN);
        }

        return qctx;
    }


    @Override
    public CommandContext visitChildren(RuleNode node) {
        switch(node.getChildCount()) {
            case 2:
                return node.getChild(0).accept(this);
            case 3:
                return node.getChild(1).accept(this);
        }

        return null;
    }

    @Override
    public CommandContext aggregateResult(CommandContext ctx, CommandContext result) {
        ctx.appendNewContext(result);
        currentContext = result;
        return result;
    }



    @Override
    public void extract(String command) {
        if(command == null) {
            return;
        }

        String[] commandTokens = command.split(";");
        for(String query : commandTokens) {
            try {
                query = query.replaceAll("\u0006", " ")
                            .replaceAll("`", "").trim();

                if(query.length() > 0) {
                    InputStream antlrInputStream =
                            new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8));

                    QDSCommandLexer lexer =
                            new QDSCommandLexer(CharStreams.fromStream(antlrInputStream, StandardCharsets.UTF_8));

                    QDSCommandParser parser = new QDSCommandParser(new CommonTokenStream(lexer));
                    parser.setBuildParseTree(true);
                    parser.removeErrorListeners();
                    parser.addErrorListener(new CommandErrorListener());

                    ParseTree tree = parser.parse();
                    CommandContext ctx = visit(tree);

                    if (ctx != null && ctx.getQueryType() != QueryType.UNKNOWN) {
                        if (root == null) {
                            root = ctx;
                            root.setAsRootNode();
                            currentContext = root;
                        } else {
                            this.aggregateResult(currentContext, ctx);
                        }
                    }
                }

            } catch (CommandParseError e) {
                throw new SQLSyntaxError(e);
            } catch (IOException ie) {
                throw new SQLSyntaxError(ie);
            }
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
                || type == Q_DROP_TABLE
                || type == Q_CREATE_VIEW
                || type == Q_DROP_VIEW;
    }

}
