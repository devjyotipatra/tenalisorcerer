package com.qubole.tenali.parse.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubole.tenali.parse.AstBaseVisitor;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;
import com.qubole.tenali.parse.AstTransformer;
import com.qubole.tenali.parse.sql.datamodel.*;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*

Create TenliAStNode for Select AST
For other AST types (insert, CTAS, CTE) update the metadata
(table name in CTAS) querycontext
 */


public class HiveAstTransformer extends AstBaseVisitor<ASTNode, TenaliAstNode> {

    CommandContext ctx;

    public HiveAstTransformer() {
        super(ASTNode.class);
    }

    public TenaliAstNode transform(ASTNode ast, CommandContext ctx) {
        this.ctx = ctx;

        System.out.println("AST   => "+ast.dump());

        if (isDDLQuery(ast.getToken().getType())
                && !isSupportedDDL(ast.getToken().getType())) {
            return new DDLNode(ast.toString(), null, null);
        }

        return parse(ast);
    }


    private boolean isDDLQuery(int operator) {
        return ((operator >= HiveParser.TOK_ALTERDATABASE_OWNER)
                && (operator <= HiveParser.TOK_ALTERVIEW_RENAME))
                || ((operator >= HiveParser.TOK_CREATEDATABASE)
                && (operator <= HiveParser.TOK_CREATEVIEW))
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

    public boolean isSupportedDDL(int operator) {
        return operator == HiveParser.TOK_CREATETABLE
                || operator == HiveParser.TOK_CREATEVIEW
                || operator == HiveParser.TOK_DROPTABLE;
    }


    private TenaliAstNode parse(ASTNode root) {
        System.out.println("PARSE PARSE .... " + root.getToken());
        TenaliAstNode node = null;
        try {
            switch (root.getToken().getType()) {
                case HiveParser.TOK_JOIN:
                case HiveParser.TOK_LEFTOUTERJOIN:
                case HiveParser.TOK_RIGHTOUTERJOIN:
                case HiveParser.TOK_FULLOUTERJOIN:
                case HiveParser.TOK_LEFTSEMIJOIN:
                case HiveParser.TOK_CROSSJOIN:
                    node = parseJoin(root.getToken().getType(), root);
                    break;
                case HiveParser.TOK_TABREF:
                    node = parseTabref(root);
                    break;
                case HiveParser.TOK_SUBQUERY:
                    node = parseSubQuery(root);
                    break;
                case HiveParser.TOK_QUERY:
                    node = parseQuery(root);
                    break;
                case HiveParser.TOK_UNIONALL:
                    node = parseUnionAll(root);
                    break;
                case HiveParser.TOK_UNIONDISTINCT:
                    node = parseUnionDistinct(root);
                    break;
                case HiveParser.TOK_CREATETABLE:
                case HiveParser.TOK_CREATEVIEW:
                    node = parseCreate(root);
                    break;
                case HiveParser.TOK_DROPTABLE:
                    node = parseDrop(root);
                    break;
                case HiveParser.TOK_LATERAL_VIEW:
                    node = parseLateralView(root);
                    break;
                default:
                    node = new UnsupportedNode("Could not handle in parse  " + root.getToken());
            }
        } catch(Exception ex) {
            System.out.println(" Error in Parse => " + ex.getMessage());
        }

        return node;
    }

    private TenaliAstNode parseLateralView(ASTNode parent) {
        System.out.println(" parseLateralView => ");
        if(parent.getChildCount() < 2) {
            return new UnsupportedNode("Lateral view should have two children ");
        }

        TenaliAstNodeList from = new TenaliAstNodeList();

        SelectNode.SelectBuilder sBuilder = new SelectNode.SelectBuilder();
        AsNode asNode = null;

        TenaliAstNodeList columns = new TenaliAstNodeList();

        ASTNode select = (ASTNode) parent.getChild(0);
        ASTNode tableNode = (ASTNode) parent.getChild(1);

        ASTNode selectExpr = (ASTNode) select.getChild(0);
        if(selectExpr != null && selectExpr.getChildCount() > 0) {
            ASTNode function = (ASTNode) selectExpr.getChild(0);

            String aliasName = null;

            for(int i=1; i<selectExpr.getChildCount(); i++) {
                ASTNode column = (ASTNode) selectExpr.getChild(i);
                if(column.getToken().getType() != HiveParser.TOK_TABALIAS) {
                    columns.add(new IdentifierNode(column.getText()));
                } else {
                    aliasName = column.getChild(0).getText();
                }
            }

            List<Node> nodeList = function.getChildren();
            sBuilder.getFrom().add(new FunctionNode("LATERAL_VIEW",
                    getOperandList(nodeList.subList(1, nodeList.size()))));
            sBuilder.setColumns(columns);

             new AsNode(aliasName, sBuilder.build());
        }

        from.add(asNode);
        from.add(parse(tableNode));

        return from;
    }


    private TenaliAstNode parseQuery(ASTNode parent) {
        System.out.println("PARSE QUERY ....  ");
        TenaliAstNode node = null;
        TenaliAstNode from = null;
        TenaliAstNodeList with = null;

        try {
            for (org.apache.hadoop.hive.ql.lib.Node child : parent.getChildren()) {
                System.out.println("PARSE QUERY ##### ....  " + child.toString());
                if (child instanceof ASTNode) {
                    System.out.println("PARSE QUERY ##### ....  " + ((ASTNode) child).getToken());
                    switch (((ASTNode) child).getToken().getType()) {
                        case HiveParser.TOK_FROM:
                            from = parseFrom((ASTNode) child);
                            break;
                        case HiveParser.TOK_INSERT:
                            node = parseInsert((ASTNode) child);
                            break;
                        case HiveParser.TOK_CTE:
                            with = parseCTE((ASTNode) child);
                            break;
                        default:
                            return new UnsupportedNode("Could not handle " + child + " inside " + parent);
                    }
                } else {
                    return new ErrorNode(child + "  Instance is not of type ASTNode");
                }
            }
        } catch (Exception ex) {
            return new ErrorNode("Instance is not of type ASTNode");
        }

        if (node instanceof SelectNode) {
            SelectNode select = (SelectNode) node;
            SelectNode.SelectBuilder builder = new SelectNode.SelectBuilder(select);
            builder.getFrom().add(from);
            builder.setWith(with);
            node = builder.build();
        }

        return node;
    }



    private TenaliAstNode parseFrom(ASTNode node) throws Exception {
        System.out.println("PARSE FROM ....");
        ASTNode child = (ASTNode) node.getChild(0);
        return parse(child);
    }

    private TenaliAstNode parseSubQuery(ASTNode node) throws Exception {
        System.out.println("PARSE SUBQUERY ....");
        TenaliAstNode nodeData = parse((ASTNode) node.getChild(0));
        String as = node.getChildren().size() == 2 ? node.getChild(1).toString().toUpperCase() : null;
        return new AsNode(as, nodeData);
    }

    private TenaliAstNode parseCreate(ASTNode node) throws Exception {
        TenaliAstNode selectNode = null;
        TenaliAstNode tableNode = null;
        String ddlToken = node.toString();
        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode
                    && ((ASTNode) child).getToken().getType() == HiveParser.TOK_QUERY) {
                selectNode = parseQuery((ASTNode) child);
            }
            if (child instanceof ASTNode
                    && ((ASTNode) child).getToken().getType() == HiveParser.TOK_TABNAME) {
                tableNode = getTabname((ASTNode) child, true);
            }
        }
        return new DDLNode(ddlToken, selectNode, tableNode);
    }

    private TenaliAstNode parseDrop(ASTNode node) throws Exception {
        TenaliAstNode tableNode = null;
        String ddlToken = node.toString();
        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode
                    && ((ASTNode) child).getToken().getType() == HiveParser.TOK_TABNAME) {
                tableNode = getTabname((ASTNode) child, true);
            }
        }
        return new DDLNode(ddlToken, null, tableNode);
    }

    private TenaliAstNode parseUnionAll(ASTNode node) throws Exception {
        TenaliAstNodeList nodeData = new TenaliAstNodeList();

        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode) {
                nodeData.add(parse((ASTNode) child));
            }
        }
        return new OperatorNode("UNION ALL", nodeData);
    }


    private TenaliAstNode parseUnionDistinct(ASTNode node) throws Exception {
        TenaliAstNodeList nodeData = new TenaliAstNodeList();

        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode) {
                nodeData.add(parseQuery((ASTNode) child));
            }
        }
        return new OperatorNode("UNION DISTINCT", nodeData);
    }

    private TenaliAstNode parseJoin(int type, ASTNode node) throws Exception {
        System.out.println("PARSE JOIN ....");
        TenaliAstNode left;
        TenaliAstNode right;
        TenaliAstNode joinCondition = null;

        if (node.getChildren().size() < 2 || node.getChildren().size() > 3) {
            return new UnsupportedNode("JoinNode children count is not equal to 3. Dump: " + node.dump());
        }


        left = parse((ASTNode) node.getChild(0));
        right = parse((ASTNode) node.getChild(1));
        if (node.getChildren().size() == 3) {
            joinCondition = parseWhereCondition((ASTNode) node.getChild(2));
        }

        return new JoinNode(getJoinType(type), left, right, joinCondition);
    }

    private TenaliAstNode parseTabref(ASTNode node) {
        System.out.println("PARSE TABLEREF ....");
        TenaliAstNode tableNode = getTabname((ASTNode) node.getChild(0), true);

        switch (node.getChildren().size()) {
            case 1:
                return tableNode;
            case 2:
                return new AsNode(node.getChild(1).toString().toUpperCase(), tableNode);
            default:
                return new UnsupportedNode(node + " has unexpected number of sub-nodes");
        }
    }

    private String getColName(ASTNode node) {

        ASTNode leftParser = node;
        while (((ASTNode) leftParser.getChild(0)).getToken().getType() != HiveParser.TOK_TABLE_OR_COL) {
            leftParser = (ASTNode) leftParser.getChild(0);
        }
        String tableName;
        tableName = getTableOrCol((ASTNode) leftParser.getChild(0));
        tableName = (leftParser.getChildren().size() == 2) ? tableName + "."
                + leftParser.getChild(1) : tableName;
        return tableName.toUpperCase();
    }

    private TenaliAstNode getTabname(ASTNode node, boolean isTable) {
        System.out.println("PARSE TABLENAME ....");
        String schemaName = null;
        String tableName;
        TenaliAstNode table;

        if (!isTable) {
            tableName = getColName(node);
            table = new IdentifierNode(tableName.toUpperCase());
        } else {
            tableName = node.getChild(0).toString();
            schemaName = (node.getChildren().size() == 2) ? tableName : ctx.getDefaultSchema();

            tableName = (node.getChildren().size() == 2) ? node.getChild(1).toString() : tableName;

            schemaName = schemaName.toUpperCase();
            tableName = tableName.toUpperCase();
            table = new IdentifierNode(schemaName + "." + tableName);
        }

        System.out.println(schemaName + "  ==  " +  tableName);

        return table;
    }

    private String getTableOrCol(ASTNode node) {
        return node.getChild(0).toString().toUpperCase();
    }

    private TenaliAstNode parseInsert(ASTNode node) throws Exception {
        System.out.println("parse INSERT  ");
        TenaliAstNode where = null;
        TenaliAstNodeList columns = new TenaliAstNodeList();
        TenaliAstNodeList groupBy = null;
        TenaliAstNodeList orderBy = null;
        TenaliAstNodeList keywords = null;
        TenaliAstNode having = null;

        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode) {
                System.out.println("PARSE INSERT  #####....  " + ((ASTNode) child).getToken());
                switch (((ASTNode) child).getToken().getType()) {
                    case HiveParser.TOK_DISTRIBUTEBY:
                    case HiveParser.TOK_DESTINATION:
                    case HiveParser.TOK_SELEXPR:
                    case HiveParser.TOK_LIMIT:
                    case HiveParser.TOK_INSERT_INTO:
                        //No operation needed currently
                        break;
                    case HiveParser.TOK_SELECTDI:
                    case HiveParser.TOK_SELECT:
                        if (((ASTNode) child).getToken().getType() == HiveParser.TOK_SELECTDI) {
                            keywords = new TenaliAstNodeList();
                            keywords.add(new IdentifierNode("DISTINCT"));
                        }
                        columns = parseSelect((ASTNode) child);
                        break;
                    case HiveParser.TOK_GROUPBY:
                        groupBy = parseGroupBy((ASTNode) child);
                        break;
                    case HiveParser.TOK_ORDERBY:
                        orderBy = parseOrderBy((ASTNode) child);
                        break;
                    case HiveParser.TOK_WHERE:
                        where = parseWhere((ASTNode) child);
                        break;
                    case HiveParser.TOK_HAVING:
                        having = parseWhere((ASTNode) child);
                        break;
                    default:
                        return new UnsupportedNode("Could not handle " + child + " inside " + node);
                }
            } else {
                return new ErrorNode("Instance is not of type ASTNode. " + node.dump());
            }
        }


        if(groupBy != null) {
            groupBy = resolvePositionalArguments(groupBy, columns);
        }

        if(orderBy != null) {
            orderBy = resolvePositionalArguments(orderBy, columns);
        }


        SelectNode.SelectBuilder builder = new SelectNode.SelectBuilder();
        builder.setWhere(where);
        builder.setGroupBy(groupBy);
        builder.setOrderBy(orderBy);
        builder.setColumns(columns);
        builder.setKeywords(keywords);
        builder.setHaving(having);
        return builder.build();
    }

    private TenaliAstNodeList parseGroupBy(ASTNode node) {
        TenaliAstNodeList groupBy = null;
        if(node.getChildCount() > 0) {
            groupBy = getOperandList(node.getChildren());
        }
        return groupBy;
    }

    private TenaliAstNode parseWhere(ASTNode node) throws Exception {
        TenaliAstNode where = null;
        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode) {
                where = parseWhereCondition((ASTNode) child);
            } else {
                where = new ErrorNode(child + " is not an instance of ASTNode");
            }
        }
        return where;
    }

    private TenaliAstNode parseWhereCondition(ASTNode node) throws Exception {
        System.out.println("parse WHERE");
        if (isOperator(node.getToken().getType())) {
            return parseOperator(node);
        } else if (node.getToken().getType() == HiveParser.TOK_FUNCTION) {
            return parseSpecialFunction(node);
        } else if (node.getToken().getType() == HiveParser.TOK_SUBQUERY_EXPR) {
            return getSubQueryExpr(node);
        } else if (node.getToken().getType() == HiveParser.TOK_TABLE_OR_COL) {
      /* Adding this case to support JOIN ON constructs.
        eg.  table1 join table2 on k.
       */
            IdentifierNode identifierNode = new IdentifierNode(getTableOrCol(node));
            TenaliAstNodeList operandList = new TenaliAstNodeList();
            operandList.add(identifierNode);
            operandList.add(identifierNode);
            return new OperatorNode("=", operandList);
        } else {
            return new ErrorNode("Could not handle " + node + " inside " + node.getParent());
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

    private TenaliAstNode parseOperator(ASTNode node) {
        TenaliAstNodeList operands = getOperandList(node.getChildren());
        return new OperatorNode(node.toString().toUpperCase(), operands);
    }

    private TenaliAstNode getOperand(ASTNode node) {
        if (isOperator(node.getToken().getType())) {
            return parseOperator(node);
        } else if (isLiteral(node.getToken().getType())) {
            return new LiteralNode(node.getText());
        } else if (node.getToken().getType() == HiveParser.TOK_TABLE_OR_COL) {
            return new IdentifierNode(getTableOrCol(node));
        } else if (node.getToken().getType() == HiveParser.TOK_FUNCTION
                || node.getToken().getType() == HiveParser.TOK_FUNCTIONDI) {
            return parseSpecialFunction(node);
        } else if (node.getToken().getType() == HiveParser.Identifier) {
            return new IdentifierNode(node.toString());
        } else if (node.getToken().getType() == HiveParser.DOT) {
            return getTabname(node, false);
        } else if (node.getToken().getType() == HiveParser.TOK_FUNCTIONSTAR) {
            TenaliAstNodeList operands = new TenaliAstNodeList();
            operands.add(new IdentifierNode("*"));
            return new OperatorNode("COUNT", operands);
        } else if (node.getToken().getType() == HiveParser.TOK_ALLCOLREF) {
            return new IdentifierNode("*");
        } else if (node instanceof ASTNode) {
            return new IdentifierNode(node.getText());
        } else {
            return new UnsupportedNode("(Unsupported Operand: " + node + ")");
        }
    }



    private TenaliAstNode parseSpecialFunction(ASTNode node) {
        String operator;
        String not = "";
        TenaliAstNodeList operands = new TenaliAstNodeList();

        if (node.getType() == HiveParser.TOK_FUNCTION) {
            operator = "FUNC";
            operands = getOperandList(node.getChildren());
        } else {
            switch (((ASTNode) node.getChild(0)).getToken().getType()) {
                case HiveParser.TOK_ISNULL:
                    operator = "IS NULL";
                    break;
                case HiveParser.TOK_ISNOTNULL:
                    operator = "IS NOT NULL";
                    break;
                default:
                    operator = node.getChild(0).toString().toUpperCase();
            }

            int i = 1;

            if (node.getChildren().size() == 1) {
                return new OperatorNode(operator, operands);
            }
            // second node is either KW_TRUE or KW_FALSE for between operator, skip that
            if (((ASTNode) node.getChild(1)).getToken().getType() == HiveParser.KW_TRUE) {
                not = "NOT ";
                i++;
            } else if (((ASTNode) node.getChild(1)).getToken().getType() == HiveParser.KW_FALSE) {
                i++;
            }
            operator = not + operator;

            operands = getOperandList(node.getChildren().subList(i, node.getChildren().size()));
        }


        if (node.getToken().getType() == HiveParser.TOK_FUNCTIONDI) {
            return new FunctionNode(operator + " DISTINCT", operands);
        } else {
            return new FunctionNode(operator, operands);
        }
    }

    private TenaliAstNodeList parseOrderBy(ASTNode node) {
        TenaliAstNodeList orderBy = null;

        if(node.getChildCount() > 0) {
            orderBy = new TenaliAstNodeList();

            for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
                if (child instanceof ASTNode) {
                    orderBy.add(getOperand((ASTNode) ((ASTNode) child).getChild(0)));
                /*if (((ASTNode) child).getToken().getType() == HiveParser.TOK_TABSORTCOLNAMEASC) {
                    orderBy.add(getOperand((ASTNode) ((ASTNode) child).getChild(0)));
                } else if (((ASTNode) child).getToken().getType() == HiveParser.TOK_TABSORTCOLNAMEDESC) {
                    TenaliAstNodeList operand = new TenaliAstNodeList();
                    operand.add(getOperand((ASTNode) ((ASTNode) child).getChild(0)));
                    orderBy.add(new OperatorNode("DESC", operand));
                } else {
                    orderBy.add(new UnsupportedNode("Could not handle " + child + " inside " + node));
                }*/
                } else {
                    orderBy.add(new ErrorNode(child + "is not instance of ASTNode"));
                }
            }
        }
        return orderBy;
    }


    private TenaliAstNodeList parseCTE(ASTNode node) throws Exception {
        TenaliAstNodeList nodeData = null;

        if(node.getChildCount() > 0) {
            nodeData = new TenaliAstNodeList();

            for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
                if (child instanceof ASTNode) {
                    if (((ASTNode) child).getToken().getType() == HiveParser.TOK_SUBQUERY) {
                        nodeData.add(parseSubQuery((ASTNode) child));
                    } else {
                        nodeData.add(new UnsupportedNode("Could not handle " + child + " inside " + node));
                    }
                } else {
                    nodeData.add(new ErrorNode(child + " is not an instance of ASTNode"));
                }
            }
        }

        return nodeData;
    }

    private TenaliAstNode getSubQueryExpr(ASTNode node) throws Exception {
        TenaliAstNode query = null;
        String tableOrCol = null;
        String subQueryOp = null;
        TenaliAstNodeList operands = new TenaliAstNodeList();

        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode) {
                switch (((ASTNode) child).getToken().getType()) {
                    case HiveParser.TOK_QUERY:
                        query = parseQuery((ASTNode) child);
                        break;
                    case HiveParser.TOK_SUBQUERY_OP:
                        subQueryOp = getTableOrCol((ASTNode) child);
                        break;
                    case HiveParser.TOK_TABLE_OR_COL:
                        tableOrCol = getTableOrCol((ASTNode) child);
                        break;
                    default:
                        return new UnsupportedNode("(Could not handle " + child + " inside " + node + ")");
                }
            }
        }

        if (tableOrCol != null) {
            operands.add(new IdentifierNode(tableOrCol.toUpperCase()));
        }
        operands.add(query);
        return new OperatorNode(subQueryOp.toUpperCase(), operands);
    }

    private TenaliAstNode parseSelExpr(ASTNode node) {
        TenaliAstNode operand = getOperand((ASTNode) node.getChild(0));

        if (node.getChildCount() == 2) {
            return new AsNode(node.getChild(1).toString().toUpperCase(), operand);
        } else {
            return operand;
        }
    }

    private TenaliAstNodeList parseSelect(ASTNode node) {
        System.out.println("PARSE SELECT ....  ");
        TenaliAstNodeList columns = new TenaliAstNodeList();
        for (org.apache.hadoop.hive.ql.lib.Node child : node.getChildren()) {
            if (child instanceof ASTNode) {
                System.out.println("PARSE SELECT @@@####....  " + ((ASTNode) child).getToken());
                switch (((ASTNode) child).getToken().getType()) {
                    case HiveParser.TOK_SELEXPR:
                        columns.add(parseSelExpr((ASTNode) child));
                        break;
                    default:
                        columns.add(
                                new UnsupportedNode("(Could not handle " + child + " inside " + node + ")"));
                }
            }
        }
        return columns;
    }


    private TenaliAstNodeList getOperandList(List<org.apache.hadoop.hive.ql.lib.Node> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return null;
        }

        TenaliAstNodeList nodeData = new TenaliAstNodeList();
        for (org.apache.hadoop.hive.ql.lib.Node node : nodes) {
            if (node instanceof ASTNode) {
                nodeData.add(getOperand((ASTNode) node));
            } else {
                nodeData.add(new ErrorNode(node + " is not an instance of ASTNode"));
            }
        }
        return nodeData;
    }


    private String getJoinType(int type) {
        switch (type) {
            case HiveParser.TOK_JOIN:
                return "INNER";
            case HiveParser.TOK_LEFTOUTERJOIN:
                return "LEFT";
            case HiveParser.TOK_RIGHTOUTERJOIN:
                return "RIGHT";
            case HiveParser.TOK_LEFTSEMIJOIN:
                return "SEMI";
            case HiveParser.TOK_CROSSJOIN:
                return "CROSS";
        }

        return "INNER";
    }


    private TenaliAstNodeList resolvePositionalArguments(TenaliAstNodeList source, TenaliAstNodeList target) {
        TenaliAstNodeList nodeList = new TenaliAstNodeList();

        for (int child_pos = 0; child_pos < source.size(); child_pos++) {
            TenaliAstNode node = source.get(child_pos);

            if (node instanceof IdentifierNode) {
                try {
                    int pos = Integer.parseInt(((IdentifierNode) node).name);
                    node = target.get(pos-1);

                    if(node instanceof AsNode) {
                        node = ((AsNode) node).value;
                    }
                } catch (NumberFormatException ex) {

                }
            }

            nodeList.add(node);
        }

        return nodeList;
    }
}