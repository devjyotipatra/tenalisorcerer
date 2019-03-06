package com.qubole.tenali.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.exception.TenaliSQLParseException;
import com.qubole.tenali.parse.lexer.DummyLexer;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public final class AbstractCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCommandHandler.class);

    private AbstractCommandHandler() {  }

    private static void prepareLexer(String command) {
        TenaliLexer lexer = new DummyLexer();
        lexer.extract(command);
    }

    private static void prepareLexer(TenaliLexer lexer, String command) {
        lexer.extract(command);
    }


    private static void prepareParser(TenaliParser parser) {
        parser.prepare();
    }



    public static class CommandParserBuilder {

        CommandType commandType;

        TenaliLexer lexer;

        TenaliParser parser;

        ImmutableList.Builder<AstTransformer> transformerBuilder = new ImmutableList.Builder();


        public CommandParserBuilder(CommandType commandType) {
            this.commandType = commandType;
        }


        public CommandParserBuilder setLexer(TenaliLexer lexer) {
            this.lexer = lexer;
            return this;
        }

        public CommandParserBuilder setParser(TenaliParser parser) {
            this.parser = parser;
            return this;
        }

        public CommandParserBuilder setTransformer(AstTransformer transformer) {
            this.transformerBuilder.add(transformer);
            return this;
        }


        public CommandContext build(String command) throws IOException {
            CommandContext rootCtx = null;

            if(lexer !=  null && lexer.getCommandType() == commandType) {
                AbstractCommandHandler.prepareLexer(lexer, command);
            } else {
                LOG.info("Lexer not provided or incompatible lexer given for " + commandType.toString());
                AbstractCommandHandler.prepareLexer(command);
            }

            rootCtx = lexer.getRootContext();
            if(rootCtx != null) {
                AbstractCommandHandler.prepareParser(parser);

                CommandContext.CommandContextIterator iterator = rootCtx.iterator();

                while(iterator.hasNext()) {
                    CommandContext cCtx  = iterator.next();

                    QueryContext qCtx = cCtx.getQueryContext();
                    TenaliParser.ParseObject parseObject = parser.parse(qCtx.getQueryType(),
                                                                        cCtx.getStmt());

                    if(parseObject.obj == null) {
                        throw new TenaliSQLParseException(parseObject.errorMessage);
                    }

                    System.out.println("TENALI AST   =>  " + parseObject.toString());

                    Object ast = parseObject.getParseObject();
                    qCtx.setParseAst(ast);
                    try {
                        ImmutableList<AstTransformer> transformers = this.transformerBuilder.build();

                        if(transformers.size() > 0) {
                            for (AstTransformer transformer : transformers) {
                                System.out.println("=====> " + transformer.getIdentifier());
                                System.out.println("=====> " + transformer.getType());
                                System.out.println("=====> " + ast.getClass());

                                Class clazz = Class.forName(transformer.getType().getCanonicalName());
                                ast = transformer.transform(clazz.cast(ast), cCtx);

                                ObjectMapper objectMapper = new ObjectMapper();
                                System.out.println("======~~~~~~~~~>>" + objectMapper.writeValueAsString(ast));
                            }

                            qCtx.setTenaliAst((TenaliAstNode) ast);
                        }

                    } catch(ClassNotFoundException ex) {
                        System.out.println(String.format("Transformation Error: Class not found for ",
                                ast.getClass().getCanonicalName(), ex.getMessage()));
                    } catch(ClassCastException ex) {
                        System.out.println("Transformation Error:  " + ex.getMessage());
                    }

                }
            }

            return rootCtx;
        }

    }
}
