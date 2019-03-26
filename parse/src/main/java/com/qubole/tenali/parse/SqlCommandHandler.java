package com.qubole.tenali.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.exception.SQLSyntaxError;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public final class SqlCommandHandler extends CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SqlCommandHandler.class);

    public SqlCommandHandler(CommandType commandType) {
        super(commandType);
    }

    @Override
    public CommandContext build(String command) {

        if (lexer != null && lexer.getCommandType() == commandType) {
            extractQueries(lexer, command);
        } else {
            LOG.info("Lexer not provided or incompatible lexer given for " + commandType.toString());
            extractQueries(command);
        }

        CommandContext rootCtx = lexer.getRootContext();
        if(rootCtx == null) {
            ///
        }

        LOG.info("Lexing successful for the query ");

        CommandContext commandContext = rootCtx;
        prepareParser(parser);

        QueryContext prevContext = null;

        while (rootCtx != null) {
            QueryType queryType = rootCtx.getQueryType();

            if (isParsableCommand(queryType)) {
                try {
                    System.out.println("Parsing ==> " +  rootCtx.getStmt());
                    QueryContext context = parser.parse(queryType, rootCtx.getStmt());
                    rootCtx.setQueryContext(context);

                    if (prevContext != null) {
                        context.setDefaultDB(prevContext.getDefaultDB());
                    }

                    prevContext = context;

                    if (context.getParseAst() == null) {
                        if (context.getErrorMessage() != null) {
                            throw new TenaliSQLParseException(context.getErrorMessage());
                        }
                    } else {
                        Object ast = context.getParseAst();

                        System.out.println("TENALI AST  << =>  " + context.toString());

                        ImmutableList<AstTransformer> transformers = this.transformerBuilder.build();

                        if (transformers.size() > 0) {
                            for (AstTransformer transformer : transformers) {
                                System.out.println("TENALI transformer  << =>  " + transformer.getIdentifier());
                                Class clazz = Class.forName(transformer.getType().getCanonicalName());
                                ast = transformer.transform(clazz.cast(ast), rootCtx);

                                ObjectMapper objectMapper = new ObjectMapper();
                                System.out.println("======~~~~~~~~~>>" + objectMapper.writeValueAsString(ast));
                            }

                            context.setTenaliAst((TenaliAstNode) ast);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    System.out.println(String.format("Transformation Error: Class not found for ",
                            rootCtx.getStmt(), ex.getMessage()));
                } catch (ClassCastException ex) {
                    System.out.println("Transformation Error:  " + ex.getMessage());
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }

            rootCtx = rootCtx.getChild();
        }

        return commandContext;
    }


    private boolean isParsableCommand(QueryType queryType) {
        if(queryType == QueryType.SET
                || queryType == QueryType.ADD_JAR) {
            return false;
        }

        return true;
    }
}
