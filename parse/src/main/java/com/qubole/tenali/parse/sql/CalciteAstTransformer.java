package com.qubole.tenali.parse.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.sql.datamodel.*;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.List;


/*

Create TenliAStNode for Select AST
For other AST types (insert, CTAS, CTE) update the metadata
(table name in CTAS) querycontext
 */

public class CalciteAstTransformer extends SqlAstBaseTransformer<SqlNode> implements SqlVisitor<TenaliAstNode> {

    CommandContext ctx = null;

    public CalciteAstTransformer() {
        super(SqlNode.class);
    }


    public TenaliAstNode transform(SqlNode ast, CommandContext ctx) {
        System.out.println("AST   => "+ast);
        this.ctx = ctx;
        return ast.accept(this);
    }


    private TenaliAstNode extractSelect(SqlNode parent, List<SqlNode> children) {
        SelectNode.SelectBuilder builder = new SelectNode.SelectBuilder();
        TenaliAstNode node = null;

        String label = null;
        for (int i = 0; i < children.size(); i++) {
            SqlNode sqlNode = children.get(i);

            if(sqlNode != null) {
                System.out.println(i + "    SELECTNODE   >>>=>  " + sqlNode.getKind() + "   " + sqlNode.getClass());
                node = sqlNode.accept(this);

                if(node != null) {
                    System.out.println(label + " ====== " + node.getClass());
                    System.out.println(label + " ~~~~~~~~~~~~~~> " + node.toString());
                }

                switch (i) {
                    case 0:
                        label = "keywords";
                        TenaliAstNodeList kNodeList = (TenaliAstNodeList) node;
                        builder.setKeywords(kNodeList);
                        break;
                    case 1:
                        label = "columns";
                        TenaliAstNodeList colNodeList = null;
                        if(!(node instanceof TenaliAstNodeList)) {
                            colNodeList = wrapInTenaliAstNodeList(node);
                        } else {
                            colNodeList = (TenaliAstNodeList) node;
                        }
                        builder.setColumns(colNodeList);
                        break;
                    case 2:
                        label = "from";
                        builder.setFrom((TenaliAstNodeList) node);
                        break;
                    case 3:
                        label = "where";
                        builder.setWhere(node);
                        break;
                    case 4:
                        label = "groupBy";
                        TenaliAstNodeList gNodeList = (TenaliAstNodeList) node;
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
                        TenaliAstNodeList wNodeList = (TenaliAstNodeList) node;
                        builder.setWindowDecls(wNodeList);
                        break;
                    case 7:
                        label = "offset";
                        break;
                    case 8:
                        label = "fetch";
                    default:
                }
            }

            else {
                System.out.println("SQL Node is NULL..");
            }
        }

        return builder.build();
    }

    private TenaliAstNode extractAs(SqlNode parent, List<SqlNode> children) {
        AsNode.AsBuilder builder = new AsNode.AsBuilder();
        System.out.println( "AS   ,  _  " + builder.toString()  + " ,  _  Children _ " + children.size());

        for (int i=0; i<children.size(); ++i) {
            SqlNode sqlNode = children.get(i);
            if (sqlNode == null) {
                System.out.println("SQL Node is NULL..");
            } else if(i == 0) {
                TenaliAstNode node = sqlNode.accept(this);
                builder.setValue(node);
            } else {
                builder.setAliasName(sqlNode.accept(this).toString());
            }
        }

        return builder.build();
    }


    private TenaliAstNode extractOperator(SqlNode parent, List<SqlNode> children) {
        SqlOperator operator = ((SqlCall) parent).getOperator();

        if(!(operator instanceof SqlOperator)) {
            return null;
        }
        OperatorNode.OperatorBuilder builder = new OperatorNode.OperatorBuilder();
        System.out.println("  )))))) Operators  ,  _  Children _  (((((( " + children.size());

        builder.setOperator(operator.getName());

        TenaliAstNodeList identifiers = new TenaliAstNodeList();

        if (children.size() > 0) {
            for (SqlNode sqlNode : children) {
                System.out.println("Operator Child  => " + sqlNode.getKind());
                if (sqlNode == null) {
                    System.out.println("SQL Node is NULL..");
                } else {
                    TenaliAstNode node = sqlNode.accept(this);
                    identifiers.add(node);
                }
            }
            builder.setOperands(identifiers);
        }

        return builder.build();
    }


    private TenaliAstNode extractFunction(SqlNode parent, List<SqlNode> children) {
        FunctionNode.FunctionBuilder builder = new FunctionNode.FunctionBuilder();

        SqlOperator operator = ((SqlCall ) parent).getOperator();
        builder.setFunctionName(operator.getName());

        TenaliAstNodeList nodeList = new TenaliAstNodeList();
        for (SqlNode child : children) {
            if (child != null) {
                nodeList.add(child.accept(this));
            }
        }
        builder.setArguments(nodeList);

        return builder.build();
    }

    private TenaliAstNode extractJoin(SqlJoin parent) {
        JoinNode.JoinBuilder builder = new JoinNode.JoinBuilder();
        builder.setJoinType(parent.getJoinType().name());
        System.out.println(parent.getJoinType().name() + " ,  _  " + builder.toString() );

        SqlNode leftNode = parent.getLeft();
        SqlNode rightNode = parent.getRight();

        assert(leftNode != null && rightNode != null);

        TenaliAstNode leftNodeAst = leftNode.accept(this);
        builder.setLeftNode(leftNodeAst);

        TenaliAstNode rightNodeAst = rightNode.accept(this);
        builder.setRightNode(rightNodeAst);

        if(parent.getCondition() != null) {
            TenaliAstNode joinCondition = parent.getCondition().accept(this);
            builder.setJoinCondition(joinCondition);
        }

        return builder.build();
    }


    /*private TenaliAstNode extractLateral(SqlNode parent, List<SqlNode> children) {
        LateralNode.LateralBuilder builder = new LateralNode.LateralBuilder();

        System.out.println("LATERAl 1 => " + children.get(0).getKind());
        if(children.size() > 1) {
            System.out.println("LATERAl 2 => " + children.get(1).getKind());
        }
        TenaliAstNode table = children.get(0).accept(this);
        builder.setTable(table);

        return builder.build();
    }*/


    private TenaliAstNode extractOrderby(SqlNode parent, List<SqlNode> children) {
        SelectNode.SelectBuilder selectBuilder = null;

        //OrderBy wraps the SelectNode and we want it the other way round in our TenaliAst.
        //So, create a SelectNode first and then add the OrderBy columns.
        SqlNode selectChild = children.get(0);
        if (selectChild != null) {
            TenaliAstNode node = selectChild.accept(this);
            selectBuilder = new SelectNode.SelectBuilder((SelectNode) node);
        }

        SqlNode orderByCols = children.get(1);
        if(orderByCols instanceof SqlNodeList) {
            TenaliAstNode node = ((SqlNodeList) orderByCols).accept(this);
            selectBuilder.getOrderBy().add(node);
        }

        return selectBuilder.build();
    }


    private TenaliAstNode extract(SqlNode parent, List<SqlNode> children) {
        TenaliAstNodeList nodeList = new TenaliAstNodeList();

        for (SqlNode child : children) {
            if (child != null) {
                nodeList.add(child.accept(this));
            }
        }

        return nodeList;
    }


    private TenaliAstNodeList extractList(SqlNode parent, TenaliAstNodeList.NodeListBuilder builder,
                                                            List<SqlNode> children) {
        System.out.println(parent.getKind() + " , list  _  " + parent.toString() + " ,  _  Children _ " + children.size());

        if (children != null) {
            for (SqlNode sqlNode : children) {
                System.out.println("   list child  __" + sqlNode.getKind());
                if (sqlNode == null) {
                    System.out.println("SQL Node is NULL..");
                } else {
                    TenaliAstNode node = sqlNode.accept(this);
                    builder.addNode(node);
                }
            }
        }

        return builder.build();
    }



    private TenaliAstNodeList wrapInTenaliAstNodeList(TenaliAstNode node) {
        TenaliAstNodeList nodes = new TenaliAstNodeList();
        nodes.add(node);
        return nodes;
    }


    public  String convertToString(TenaliAstNode node) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(node);
    }



    @Override public TenaliAstNode visit(SqlLiteral literal) {
        return new LiteralNode(literal.getValue().toString());
    }

    @Override public TenaliAstNode visit(SqlCall call) {
        TenaliAstNode node = null;

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
                    //node = extractLateral(call, call.getOperandList());
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
                default:
                    node = extractOperator(call, call.getOperandList());
                    if(node == null) {
                        System.out.println("Going Nowhere .... ");
                        node = extract(call, call.getOperandList());
                    }
                /*case CASE:
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
                        System.out.println("Going Nowhere .... ");
                        node = extract(call, call.getOperandList());
                    }*/
            }
        }

        return node;
    }

    @Override public TenaliAstNode visit(SqlNodeList nodeList) {
        System.out.println("SqlNodeList ---- " + nodeList.getList());
        if(nodeList == null || nodeList.getList().isEmpty()) {
            return null;
        }
        return extractList(nodeList, new TenaliAstNodeList.NodeListBuilder(), nodeList.getList());
    }

    @Override public TenaliAstNode visit(SqlIdentifier id) {
        return new IdentifierNode(id.toString());
    }

    @Override public TenaliAstNode visit(SqlDataTypeSpec type) {
        System.out.println("type ---- " + type.getKind() + "   " + type.getClass());
        return null;
    }

    @Override public TenaliAstNode visit(SqlDynamicParam param) {
        System.out.println("param  " + param.getKind() + "   " + param.getClass());
        return null;
    }

    @Override public TenaliAstNode visit(SqlIntervalQualifier intervalQualifier) {
        System.out.println("intervalQualifier  " + intervalQualifier.getKind() + "   " + intervalQualifier.getClass());
        return null;
    }
}
