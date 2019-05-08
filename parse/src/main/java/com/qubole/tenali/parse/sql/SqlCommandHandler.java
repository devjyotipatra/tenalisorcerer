package com.qubole.tenali.parse.sql;

import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.TenaliTransformer;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import com.qubole.tenali.parse.sql.datamodel.ErrorNode;
import com.qubole.tenali.parse.sql.datamodel.MetaNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.parse.sql.handler.TenaliCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class SqlCommandHandler extends TenaliCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SqlCommandHandler.class);

    public SqlCommandHandler(CommandType commandType) {
        super(commandType);
    }

    @Override
    public CommandContext build(String command) {
        ImmutableList<TenaliTransformer> transformers = transformerBuilder.build();

        if (lexer != null && lexer.getCommandType() == commandType) {
            extractQueries(lexer, command);
        } else {
            LOG.info("Lexer not provided or incompatible lexer given for " + commandType.toString());
            extractQueries(command);
        }

        CommandContext rootCtx = lexer.getRootContext();
        if(rootCtx == null) {
            LOG.error("Lexer could not find a valid SQL query string");
            return rootCtx;
        }

        CommandContext commandContext = rootCtx;
        prepareParser(parser);

        CommandContext ctx = rootCtx;
        while (ctx != null) {
            QueryType queryType = ctx.getQueryType();

            try {
                QueryContext context = ctx.getQueryContext();
                if(context == null) {
                    context = parser.parse(queryType, ctx.getStmt());
                    ctx.setQueryContext(context);

                    if (ctx.hasParent()) {
                        context.setDefaultDB(ctx.getParent().getQueryContext().getDefaultDB());
                    }
                }

                Object ast = context.getParseAst();

                if (!(ctx.isDDLQuery()
                        || (ast instanceof MetaNode)
                        || (ast instanceof ErrorNode))) {

                    for (TenaliTransformer transformer : transformers) {
                        Class clazz = Class.forName(transformer.getType().getCanonicalName());
                        ast = transformer.transform(clazz.cast(ast), ctx);

                        //ObjectMapper objectMapper = new ObjectMapper();
                        //String res = objectMapper.writeValueAsString(ast);
                        //LOG.info(res);
                    }
                }

                if(ast instanceof TenaliAstNode) {
                    context.setTenaliAst((TenaliAstNode) ast);
                }

            } catch (ClassNotFoundException ex) {
                LOG.error(String.format("Transformation Error: Class not found for ",
                        ctx.getStmt(), ex.getMessage()));
            } catch (ClassCastException ex) {
                LOG.error("Transformation Error:  " + ex.getMessage());
                ex.printStackTrace();
            } catch (TenaliSQLParseException ep) {
                LOG.error("Parsing Error:  " + ep.getMessage());
                ctx.setQueryContext(new QueryContext(new ErrorNode("ParseException, " + ep.getMessage())));
            } catch (Exception ed) {
                LOG.error("General Error:  " + ed.getMessage());
                ed.printStackTrace();
                ctx.setQueryContext(new QueryContext(new ErrorNode("ParseException, " + ed.getMessage())));
            }

            ctx = ctx.getChild();
        }

        return commandContext;
    }
}
