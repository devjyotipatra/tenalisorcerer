package com.qubole.tenali.parse.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubole.tenali.parse.parser.config.CommandType;
import com.qubole.tenali.parse.parser.config.CommandContext;
import com.qubole.tenali.parse.parser.config.QueryContext;
import com.qubole.tenali.parse.parser.config.QueryType;
import com.qubole.tenali.parse.parser.lexer.DummyLexer;
import com.qubole.tenali.parse.parser.lexer.TenaliLexer;
import com.qubole.tenali.parse.parser.sql.datamodel.TenaliAstNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import java.io.IOException;

public final class AbstractCommandHandler {

    private CommandContext rootCtx = null;

    private int numParseTrees;

    private AbstractCommandHandler() {  }

    private void prepareLexer(String command) {
        TenaliLexer lexer = new DummyLexer();
        lexer.prepare(command);
    }

    private void prepareLexer(TenaliLexer lexer, String command) {
        lexer.prepare(command);
    }


    private void prepareParser(TenaliParser parser) {
        parser.prepare();
    }

    private void setRootContext(CommandContext rootCtx) {
        this.rootCtx = rootCtx;

        CommandContext.CommandContextIterator iter = rootCtx.iterator();
        while(iter.hasNext()) {
            numParseTrees += 1;
            iter.next();
        }
    }


    private QueryContext getQueryContext(TenaliParser.ParseObject parseObject) {
        QueryContext qCtx = null;

        QueryType queryType = parseObject.getQueryType();
        Object obj = parseObject.getParseObject();

        if(obj != null) {
            System.out.println("`````` == "+obj.getClass());
            if(obj instanceof SqlNode) {
                qCtx = new QueryContext<SqlNode>(queryType, (SqlNode) obj);
            } else if(obj instanceof ASTNode) {
                qCtx = new QueryContext<ASTNode>(queryType, (ASTNode) obj);
            } else {
                System.out.println("Not Implemented...");
            }
        }

        return qCtx;
    }


    /*public void visitParseTrees() {
        CommandContext.CommandContextIterator iter = rootCtx.iterator();

        while(iter.hasNext()) {

        }
    }*/

    public String getIthStatement(int index) {
        int ctree = 0;
        CommandContext.CommandContextIterator iter = rootCtx.iterator();

        while(iter.hasNext()) {
            ctree += 1;

            CommandContext ctx = iter.next();

            if(index - ctree == 0) {
                return ctx.getStmt();
            }
        }

        return null;
    }


    public static class CommandParserBuilder {

        CommandType commandType;

        TenaliLexer lexer;

        TenaliParser parser;

        AstTransformer transformer;

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
            this.transformer = transformer;
            return this;
        }


        public AbstractCommandHandler build(String command) throws IOException {
            AbstractCommandHandler handler = new AbstractCommandHandler();

            if(lexer !=  null && lexer.getCommandType() == commandType) {
                handler.prepareLexer(lexer, command);
            } else {
                //if a lexer is  not defined, use dummy lexer
                System.out.println("Incompatible Lexer for " + commandType.toString());
                handler.prepareLexer(new DummyLexer(), command);
            }

            CommandContext rootCtx = lexer.getRootContext();

            if(rootCtx != null) {
                handler.prepareParser(parser);

                CommandContext.CommandContextIterator iterator = rootCtx.iterator();

                while(iterator.hasNext()) {
                    CommandContext cCtx  = iterator.next();
                    TenaliParser.ParseObject parseObject = parser.parse(cCtx.getQueryType(), cCtx.getStmt());

                    QueryContext qCtx = handler.getQueryContext(parseObject);
                    System.out.println("TENALI AST   =>  " + qCtx.getAst().toString());
                    TenaliAstNode root = transformer.transform(qCtx.getAst());

                    ObjectMapper objectMapper = new ObjectMapper();
                    System.out.println("======~~~~~~~~~>>" + objectMapper.writeValueAsString(root));

                }
            }

            handler.setRootContext(rootCtx);
            return handler;
        }

    }
}
