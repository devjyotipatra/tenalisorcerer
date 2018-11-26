package com.qubole.tenali.parse.parser.sql;

import com.qubole.tenali.parse.parser.AstTransformer;
import com.qubole.tenali.parse.parser.config.QueryContext;
import com.qubole.tenali.parse.parser.config.QueryType;
import com.qubole.tenali.parse.parser.sql.datamodel.*;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.List;
import java.util.Stack;

/*

Create TenliAStNode for Select AST
For other AST types (insert, CTAS, CTE) update the metadata
(table name in CTAS) querycontext
 */


public class HiveAstTransformer implements AstTransformer<ASTNode> {

    QueryContext queryContext = null;

    Stack<ASTNode> stack = new Stack();

    public TenaliAstNode transform(ASTNode ast, QueryContext qCtx) {
        System.out.println("AST   => "+ast.dump());
        queryContext = qCtx;
        return visitNode(ast);
    }


    public TenaliAstNode visitNode(ASTNode ast) {
        ASTNode node = (ASTNode)  ast.getChild(0);
        stack.push(node);

        return visitNode();
    }


    public TenaliAstNode visitNode() {
        //BUG:: CHANGE THIS  -> SHOULD NOT BE NULL
        TenaliAstNode fromNode = null;
        while(!stack.empty()) {
            ASTNode ast = stack.pop();

            try {
                if(ast.getChildCount() > 0) {
                    for (Node child : ast.getChildren()) {
                        if (child instanceof ASTNode) {
                            System.out.println("------->> " + ((ASTNode) child).getToken().getText());
                            ASTNode node = (ASTNode) child;

                            switch (node.getToken().getType()) {
                                // Hive inserts data for Select stmts into a temp location and hence
                                // every Select query has an Insert Node at the root level. So, we should
                                // distinguish between Select stmts and real Insert stmts here.
                                case HiveParser.TOK_INSERT:
                                    if (queryContext.getQueryType() == QueryType.SELECT) {
                                        extractSelect(node);
                                    } else {
                                        //
                                    }
                                    break;
                                case HiveParser.TOK_FROM:
                                    ASTNode tabRefNode = (ASTNode) node.getChild(0);
                                    parseNode(tabRefNode);
                                    break;
                                default:
                                    stack.push(node);
                            }
                        } else {
                            return new ErrorNode("Instance is not of type ASTNode " + child.getName());
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }

        return null;
    }


    private TenaliAstNode extractSelect(ASTNode root) {
        TenaliAstNode node = null;

        SelectNode.SelectBuilder builder = new SelectNode.SelectBuilder();
        TenaliAstNodeList keywords = new TenaliAstNodeList();
        TenaliAstNodeList columns = null;

        for (Node child: root.getChildren()) {
            switch (((ASTNode) child).getToken().getType()) {
                case HiveParser.TOK_SELECTDI:
                case HiveParser.TOK_SELECT:
                    if (((ASTNode) child).getToken().getType() == HiveParser.TOK_SELECTDI) {
                        keywords.add(new IdentifierNode("DISTINCT"));
                        builder.setKeywords(keywords);
                    }

                    columns = extractChildren((ASTNode) child, HiveParser.TOK_SELEXPR);

            }
        }


        return builder.build();
    }


    public TenaliAstNodeList extractChildren(ASTNode node, int hiveToken) {
        TenaliAstNodeList children = new TenaliAstNodeList();

        for (Node child: node.getChildren()) {
            if (child instanceof ASTNode) {
                if(HiveParser.TOK_SELEXPR == hiveToken) {
                    children.add(parseOperand((ASTNode) child));
                } else {
                        //columns.add(
                        //        new UnsupportedNode("(Could not handle " + child + " inside " + node + ")"));
                }
            }
        }

        return children;
    }



    private TenaliAstNode parseOperator(ASTNode root) {
        TenaliAstNodeList operands = new TenaliAstNodeList();

        List<org.apache.hadoop.hive.ql.lib.Node> nodes = root.getChildren();
        if (nodes == null || nodes.size() == 0) {
            return null;
        }

        for (org.apache.hadoop.hive.ql.lib.Node node: nodes) {
            if (node instanceof ASTNode && !isLiteral(((ASTNode) node).getToken().getType())) {
                operands.add(parseOperand((ASTNode) node));
            } else {
                //nodeData.add(new ErrorNode(node + " is not an instance of ASTNode"));
            }
        }

        return new OperatorNode(root.getName().toUpperCase(), operands);

    }


    private TenaliAstNode parseFunction(ASTNode root) {
        TenaliAstNodeList operands = new TenaliAstNodeList();

        String operator = root.getName().toUpperCase();
        if (root.getType() != HiveParser.TOK_FUNCTION
                && root.getType() != HiveParser.TOK_FUNCTIONDI) {
            switch (((ASTNode) root.getChild(0)).getToken().getType()) {
                case HiveParser.TOK_ISNULL:
                    operator = "IS NULL";
                    break;
                case HiveParser.TOK_ISNOTNULL:
                    operator = "IS NOT NULL";
                    break;
                default:
                    operator = root.getChild(0).toString().toUpperCase();
            }
        }

        List<org.apache.hadoop.hive.ql.lib.Node> nodes = root.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node node: nodes) {
            if (node instanceof ASTNode && !isLiteral(((ASTNode) node).getToken().getType())) {
                operands.add(parseOperand((ASTNode) node));
            } else {
                //nodeData.add(new ErrorNode(node + " is not an instance of ASTNode"));
            }
        }

        return new FunctionNode(operator, operands);
    }



    private TenaliAstNode parseTableOrColumnName(ASTNode node) {
        ASTNode leftParser = node;
        ASTNode leftChild = (ASTNode) leftParser.getChild(0);
        while (leftChild.getToken().getType() != HiveParser.TOK_TABLE_OR_COL
                && leftChild.getToken().getType() != HiveParser.TOK_TABNAME) {
            leftParser = (ASTNode) leftParser.getChild(0);
            leftChild = (ASTNode) leftParser.getChild(0);
        }

        String name = ((ASTNode) leftParser.getChild(0))
                .toString().toUpperCase();
        String alias = (leftParser.getChildren().size() == 2) ? ((ASTNode) leftParser.getChild(0))
                .toString().toUpperCase() : null;

        if(alias == null) {
            return new IdentifierNode(name);
        }

        return new AsNode(alias, new IdentifierNode(name));
    }


    private TenaliAstNode parseOperand(ASTNode node) {
        if (isOperator(node.getToken().getType())) {
            return parseOperator(node);
        } else if(node.getToken().getType() == HiveParser.TOK_FUNCTION
                || node.getToken().getType() == HiveParser.TOK_FUNCTIONDI) {
            return parseFunction(node);
        } else if (node.getToken().getType() == HiveParser.DOT) {
            return parseTableOrColumnName(node);
        } else if (isLiteral(node.getToken().getType())) {
            return new LiteralNode(node.toString());
        } else if (node.getToken().getType() == HiveParser.TOK_TABLE_OR_COL
                || node.getToken().getType() == HiveParser.Identifier) {
            return new IdentifierNode(node.getChild(0).toString().toUpperCase());
        } else if (node.getToken().getType() == HiveParser.TOK_FUNCTIONSTAR
                || node.getToken().getType() == HiveParser.TOK_ALLCOLREF) {
            return new IdentifierNode("*");
        } else if (node instanceof ASTNode) {
            return new IdentifierNode(node.getText());
        }

        return new ErrorNode("Not able to find the operand type " + node.toString());
    }


    private TenaliAstNode parseNode(ASTNode node) throws Exception {
        if (isUnsupportedDDLQuery(node.getToken().getType())) {
            return new ErrorNode("Unsupported DDL query " + node.getName());
        }
        switch (node.getToken().getType()) {
            case HiveParser.TOK_JOIN:
            case HiveParser.TOK_LEFTOUTERJOIN:
            case HiveParser.TOK_RIGHTOUTERJOIN:
            case HiveParser.TOK_FULLOUTERJOIN:
            case HiveParser.TOK_LEFTSEMIJOIN:
            case HiveParser.TOK_CROSSJOIN:
                //return parseJoin(node);
            case HiveParser.TOK_TABREF:
                return parseTableOrColumnName(node);
            case HiveParser.TOK_SUBQUERY:
                //return parseSubQuery(node);
            case HiveParser.TOK_QUERY:
                //return parseQuery(node);
            case HiveParser.TOK_UNIONALL:
                //return parseUnionAll(node);
            case HiveParser.TOK_UNIONDISTINCT:
                //return parseUnionDistinct(node);
            case HiveParser.TOK_CREATETABLE:
            case HiveParser.TOK_CREATEVIEW:
                //return parseCreate(node);
            case HiveParser.TOK_DROPTABLE:
                //return parseDrop(node);
            default:
                return new ErrorNode("Could not handle " + node.getToken());
        }
    }


    private boolean isOperator(int operator) {
        return isRelationalOperator(operator)
                || isLogicalOperator(operator)
                || isArithmeticOperator(operator)
                || (operator == HiveParser.KW_LIKE);
    }

    private boolean isRelationalOperator(int operator) {
        return (operator == HiveParser.GREATERTHAN)
                || (operator == HiveParser.LESSTHAN)
                || (operator == HiveParser.LESSTHANOREQUALTO)
                || (operator == HiveParser.GREATERTHANOREQUALTO)
                || (operator == HiveParser.EQUAL)
                || (operator == HiveParser.NOTEQUAL);
    }

    private boolean isLogicalOperator(int operator) {
        return (operator == HiveParser.KW_AND)
                || (operator == HiveParser.KW_OR)
                || (operator == HiveParser.KW_NOT);
    }

    private boolean isArithmeticOperator(int operator) {
        return (operator == HiveParser.PLUS)
                || (operator == HiveParser.MINUS)
                || (operator == HiveParser.STAR)
                || (operator == HiveParser.DIVIDE)
                || (operator == HiveParser.MOD);
    }

    private boolean isLiteral(int operator) {
        return (operator == HiveParser.Number)
                || (operator == HiveParser.StringLiteral)
                || (operator == HiveParser.BigintLiteral)
                || (operator == HiveParser.TinyintLiteral)
                || (operator == HiveParser.SmallintLiteral)
                || (operator == HiveParser.KW_TRUE)
                || (operator == HiveParser.KW_FALSE)
                || (operator == HiveParser.TOK_NULL);
    }


    private boolean isUnsupportedDDLQuery(int operator) {
        return ((operator >= HiveParser.TOK_ALTERDATABASE_OWNER)
                && (operator <= HiveParser.TOK_ALTERVIEW_RENAME))
                || (operator >= HiveParser.TOK_CREATEDATABASE)
                || ((operator >= HiveParser.TOK_DROPDATABASE)
                && (operator <= HiveParser.TOK_DROPVIEW))
                || ((operator >= HiveParser.TOK_SHOWCOLUMNS)
                && (operator <= HiveParser.TOK_SHOW_TRANSACTIONS))
                || ((operator >= HiveParser.TOK_GRANT)
                && (operator <= HiveParser.TOK_GRANT_WITH_OPTION))
                || ((operator >= HiveParser.TOK_REVOKE)
                && (operator <= HiveParser.TOK_REVOKE_ROLE))
                || (operator == HiveParser.TOK_TRUNCATETABLE)
                || (operator == HiveParser.TOK_SWITCHDATABASE);
    }
}
