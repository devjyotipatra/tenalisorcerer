package com.qubole.tenali.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class SqlCommandHandler extends CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SqlCommandHandler.class);

    public SqlCommandHandler(CommandType commandType) {
        super(commandType);
    }

    @Override
    public CommandContext build(String command) {
        ImmutableList<AstTransformer> transformers = transformerBuilder.build();

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

        while (rootCtx != null) {
            QueryType queryType = rootCtx.getQueryType();
            QueryContext context = null;
            Object ast = null;
            try {
                context = parser.parse(queryType, rootCtx.getStmt());
                rootCtx.setQueryContext(context);

                if (rootCtx.hasParent() && rootCtx.getParent().getQueryContext() != null) {
                    context.setDefaultDB(rootCtx.getParent().getQueryContext().getDefaultDB());
                }

                ast = context.getParseAst();

                LOG.debug("TENALI AST  << .. =>  " + ast.toString());

                if (transformers.size() > 0) {
                    for (AstTransformer transformer : transformers) {
                        Class clazz = Class.forName(transformer.getType().getCanonicalName());
                        ast = transformer.transform(clazz.cast(ast), rootCtx);

                        ObjectMapper objectMapper = new ObjectMapper();
                        LOG.info(String.format("Transformed using %s => %s", transformer.getIdentifier(),
                                objectMapper.writeValueAsString(ast)));
                    }
                }

                context.setTenaliAst((TenaliAstNode) ast);

            } catch (ClassNotFoundException ex) {
                LOG.error(String.format("Transformation Error: Class not found for ",
                        rootCtx.getStmt(), ex.getMessage()));
            } catch (ClassCastException ex) {
                LOG.error("Transformation Error:  " + ex.getMessage());
            } catch (TenaliSQLParseException ep) {
                LOG.error("Parsing Error:  " + ep.getMessage());
            } catch (Exception ee) {
                LOG.error("Json Error:  " + ee.getMessage());
            }

            rootCtx = rootCtx.getChild();
        }

        return commandContext;
    }
}
