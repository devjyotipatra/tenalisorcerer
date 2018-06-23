package com.qubole.tenali.parse.parser.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubole.tenali.parse.parser.sql.datamodel.*;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.List;


public class CalciteAstToBaseAstConverter implements SqlVisitor<BaseAstNode> {

    public  String convertToString(BaseAstNode node) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(node);
    }


    private BaseAstNodeList wrapInBaseAstNodeList(BaseAstNode node) {
        BaseAstNodeList nodes = new BaseAstNodeList();
        nodes.add(node);
        return nodes;
    }

    private BaseAstNode extractSelect(SqlNode parent, List<SqlNode> children) {
        SelectNode.SelectBuilder builder = new SelectNode.SelectBuilder();
        BaseAstNode node = null;

        int i = 0;
        String label = null;
        for (SqlNode sqlNode : children) {
            if(sqlNode != null) {
                System.out.println(i + "    SELECTNODE   =>  " + sqlNode.getKind() + "   " + sqlNode.getClass());
                node = sqlNode.accept(this);
                switch (i) {
                    case 0:
                        label = "keywords";
                        BaseAstNodeList kNodeList = (BaseAstNodeList) node;
                        if(kNodeList.size() > 0) {
                            builder.setKeywords(kNodeList);
                        }
                        break;
                    case 1:
                        label = "columns";
                        BaseAstNodeList colNodeList = null;
                        if(!(node instanceof BaseAstNodeList)) {
                            colNodeList = wrapInBaseAstNodeList(node);
                        } else {
                            colNodeList = (BaseAstNodeList) node;
                        }
                        builder.setColumns(colNodeList);
                        break;
                    case 2:
                        label = "from";
                        builder.setFrom(node);
                        break;
                    case 3:
                        label = "where";
                        builder.setWhere(node);
                        break;
                    case 4:
                        label = "groupBy";
                        BaseAstNodeList gNodeList = (BaseAstNodeList) node;
                        if(gNodeList.size() > 0) {
                            builder.setGroupBy(gNodeList);
                        }
                        break;
                    case 5:
                        label = "having";
                        builder.setHaving(node);
                        break;
                    case 6:
                        label = "windowDecls";
                        BaseAstNodeList wNodeList = (BaseAstNodeList) node;
                        if(wNodeList.size() > 0) {
                            builder.setWindowDecls(wNodeList);
                        }
                        break;
                    case 7:
                        label = "offset";
                        break;
                    case 8:
                        label = "fetch";
                    default:
                }

                System.out.println(label + " ====== " + node.getClass());
                System.out.println(label + " ~~~~~~~~~~~~~~> " + node.toString());
            }

            else {
                System.out.println("SQL Node is NULL..");
            }

            ++ i;
        }

        return builder.build();
    }


    private BaseAstNode extractAs(SqlNode parent, List<SqlNode> children) {
        AsNode.AsBuilder builder = new AsNode.AsBuilder();
        System.out.println(parent.getKind() + " ,  _  " + builder.toString()  + " ,  _  Children _ " + children.size());

        for (int i=0; i<children.size(); ++i) {
            SqlNode sqlNode = children.get(i);
            if (sqlNode == null) {
                System.out.println("SQL Node is NULL..");
            } else if(i == 0) {
                BaseAstNode node = sqlNode.accept(this);
                builder.setValue(node);
            } else {
                builder.setAliasName(sqlNode.accept(this).toString());
            }
        }

        return builder.build();
    }


    private BaseAstNode extractOperator(String operatorName, List<SqlNode> children) {
        OperatorNode.OperatorBuilder builder = new OperatorNode.OperatorBuilder();
        System.out.println("  )))))) Operators  ,  _  Children _  (((((( " + children.size());

        builder.setOperator(operatorName);

        BaseAstNodeList identifiers = new BaseAstNodeList();

        if (children.size() > 0) {
            for (SqlNode sqlNode : children) {
                System.out.println("Operator Child  => " + sqlNode.getKind());
                if (sqlNode == null) {
                    System.out.println("SQL Node is NULL..");
                } else {
                    BaseAstNode node = sqlNode.accept(this);
                    identifiers.add(node);
                }
            }
            builder.setOperands(identifiers);
        }

        return builder.build();
    }



    private BaseAstNodeList extractList(SqlNode parent, BaseAstNode.Builder builder,
                                List<SqlNode> children) {
        System.out.println(parent.getKind() + " , list  _  " + parent.toString() + " ,  _  Children _ " + children.size());

        BaseAstNodeList nodeList = new BaseAstNodeList();

        if (children.size() > 0) {
            for (SqlNode sqlNode : children) {
                System.out.println("   list child  __" + sqlNode.getKind());
                if (sqlNode == null) {
                    System.out.println("SQL Node is NULL..");
                } else {
                    BaseAstNode node = sqlNode.accept(this);
                    nodeList.add(node);
                }
            }
        }

        return nodeList;
    }


    private BaseAstNode extractJoin(SqlJoin parent) {
        JoinNode.JoinBuilder builder = new JoinNode.JoinBuilder();
        builder.setJoinType(parent.getJoinType().name());
        System.out.println(parent.getJoinType().name() + " ,  _  " + builder.toString() );

        SqlNode leftNode = parent.getLeft();
        if(leftNode != null) {
            BaseAstNode leftNodeAst = leftNode.accept(this);
            builder.setLeftNode(leftNodeAst);
        }

        SqlNode rightNode = parent.getRight();
        if(rightNode != null) {
            BaseAstNode rightNodeAst = rightNode.accept(this);
            builder.setRightNode(rightNodeAst);
        }

        if(parent.getCondition() != null) {
            BaseAstNode joinCondition = parent.getCondition().accept(this);
            builder.setJoinCondition(joinCondition);
        }

        return builder.build();
    }


    private BaseAstNode extractLateral(SqlNode parent, List<SqlNode> children) {
        LateralNode.LateralBuilder builder = new LateralNode.LateralBuilder();

        System.out.println("LATERAl 1 => " + children.get(0).getKind());
        if(children.size() > 1) {
            System.out.println("LATERAl 2 => " + children.get(1).getKind());
        }
        BaseAstNode table = children.get(0).accept(this);
        builder.setTable(table);

        return builder.build();
    }


    private BaseAstNode extractFunction(SqlNode parent, List<SqlNode> children) {
        FunctionNode.FunctionBuilder builder = new FunctionNode.FunctionBuilder();

        SqlOperator operator = ((SqlCall ) parent).getOperator();
        builder.setFunctionName(operator.getName());

        BaseAstNodeList nodeList = new BaseAstNodeList();
        for (SqlNode child : children) {
            if (child != null) {
                nodeList.add(child.accept(this));
            }
        }
        builder.setArguments(nodeList);

        return builder.build();
    }


    private BaseAstNode extractOrderby(SqlNode parent, List<SqlNode> children) {
        SelectNode.SelectBuilder selectBuilder = null;

        //OrderBy wraps the SelectNode and we want it the other way round in our BaseAst.
        //So, create a SelectNode first and then add the OrderBy columns.
        SqlNode selectChild = children.get(0);
        if (selectChild != null) {
            BaseAstNode node = selectChild.accept(this);
            selectBuilder = new SelectNode.SelectBuilder((SelectNode) node);
        }

        SqlNode orderByCols = children.get(1);
        if(orderByCols instanceof SqlNodeList) {
            BaseAstNode node = orderByCols.accept(this);
            selectBuilder.setOrderBy(node);
        }

        return selectBuilder.build();
    }


    private BaseAstNode extract(SqlNode parent, List<SqlNode> children) {
        BaseAstNodeList nodeList = new BaseAstNodeList();

        for (SqlNode child : children) {
            if (child != null) {
                nodeList.add(child.accept(this));
            }
        }

        return nodeList;
    }


    @Override public BaseAstNode visit(SqlLiteral literal) {
        return new LiteralNode(literal.getValue());
    }

    @Override public BaseAstNode visit(SqlCall call) {
        BaseAstNode node = null;

        System.out.println("call ===== "+ call.getKind()  + "   " + call.getOperator().getClass());

        if(call.getOperator() instanceof SqlFunction) {
            node = extractFunction(call, call.getOperandList());
        } else {
            System.out.println("call switching===== ");
            switch (call.getKind()) {
                case SELECT:
                    node = extractSelect(call, call.getOperandList());
                    break;
                case AS:
                    node = extractAs(call, call.getOperandList());
                    break;
                case LATERAL:
                    node = extractLateral(call, call.getOperandList());
                    break;
                case JOIN:
                    node = extractJoin((SqlJoin) call);
                    break;
                case COLLECTION_TABLE:
                    node = extract(call, call.getOperandList());
                    break;
                case ORDER_BY:
                    node = extractOrderby(call, call.getOperandList());
                    break;
                case CASE:
                case OVER:
                case WINDOW:
                    SqlOperator winOperator = call.getOperator();
                    node = extractOperator(winOperator.getName(), call.getOperandList());
                    break;
                default:
                    SqlOperator operator = call.getOperator();
                    if(operator instanceof SqlBinaryOperator) {
                        node = extractOperator(operator.getName(), call.getOperandList());
                    } else {
                        node = extract(call, call.getOperandList());
                    }
            }
        }

        return node;
    }

    @Override public BaseAstNode visit(SqlNodeList nodeList) {
        System.out.println("SqlNodeList ---- " + nodeList.getList());
        return extractList(nodeList, new BaseAstNodeList.NodeListBuilder(), nodeList.getList());
    }

    @Override public BaseAstNode visit(SqlIdentifier id) {
        return new IdentifierNode(id.toString());
    }

    @Override public BaseAstNode visit(SqlDataTypeSpec type) {
        System.out.println("type ---- " + type.getKind() + "   " + type.getClass());
        return null;
    }

    @Override public BaseAstNode visit(SqlDynamicParam param) {
        System.out.println("param  " + param.getKind() + "   " + param.getClass());
        return null;
    }

    @Override public BaseAstNode visit(SqlIntervalQualifier intervalQualifier) {
        System.out.println("intervalQualifier  " + intervalQualifier.getKind() + "   " + intervalQualifier.getClass());
        return null;
    }
}
