package com.qubole.tenali.parse.parser.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubole.tenali.parse.parser.sql.datamodel.*;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.List;

import static java.util.Arrays.asList;

public class CalciteAstToBaseAstConverter implements SqlVisitor<BaseAstNode> {
    private StringBuilder sb = new StringBuilder();
    private int indent = 0;

    public  String convertToString(BaseAstNode node) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(node);
    }


    //------------------------------------START--------------------------------------------

    private BaseAstNode extract(SqlNode parent) {
        return extract(parent, null);
    }

    private BaseAstNode extract(SqlNode parent, BaseAstNode.Builder builder, SqlNode... nodes) {
        return extract(parent, builder, asList(nodes));
    }

    private BaseAstNode extractSelect(SqlNode parent, SelectNode.SelectBuilder builder,
                                List<SqlNode> children) {
        //System.out.println(parent.getKind() + " ,  _  " + builder.toString()  + " ,  _  Children _ " + children.size());
        BaseAstNode node = null;

        int i = 0;
        String label = null;
        for (SqlNode sqlNode : children) {
            if(sqlNode != null) {
                System.out.println(i + "     SQLNODE   =>  " + sqlNode.getKind());
                node = sqlNode.accept(this);

                switch (i) {
                    case 0:
                        label = "keywords";
                        builder.setKeywords((BaseAstNodeList) node);
                        break;
                    case 1:
                        label = "columns";
                        builder.setColumns((BaseAstNodeList) node);
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
                        builder.setGroupBy((BaseAstNodeList) node);
                        break;
                    case 5:
                        label = "having";
                        builder.setHaving(node);
                        break;
                    case 6:
                        label = "windowDecls";
                        builder.setWindowDecls((BaseAstNodeList) node);
                        break;
                    case 7:
                        label = "orderBy";
                        builder.setOrderBy((BaseAstNodeList) node);
                        break;
                    case 8:
                        label = "offset";
                        break;
                    case 9:
                        label = "fetch";
                    default:
                }

                System.out.println("LABEL SELECT .. " + label + "     " + sqlNode.getClass());
            }

            else {
                System.out.println("SQL Node is NULL..");
            }

            ++ i;
        }

        //System.out.println("BUILD SELECT ========>   " + builder.build().toString());

        return builder.build();
    }


    private BaseAstNode extractAs(SqlNode parent, AsNode.AsBuilder builder,
                                List<SqlNode> children) {
        System.out.println(parent.getKind() + " ,  _  " + builder.toString()  + " ,  _  Children _ " + children.size());

        for (int i=0; i<children.size(); ++i) {
            SqlNode sqlNode = children.get(i);
            if (sqlNode == null) {
                System.out.println("SQL Node is NULL..");
            } else if(i == 0) {
                builder.setValue(sqlNode.accept(this).toString());
            } else {
                builder.setAliasName(sqlNode.accept(this).toString());
            }
        }

        return builder.build();
    }


    private BaseAstNode extractOperator(SqlOperator parent, OperatorNode.OperatorBuilder builder,
                                List<SqlNode> children) {
        System.out.println("Operators  ,  _  Children _ " + children.size());

        builder.setOperator(parent.getKind().lowerName);

        BaseAstNodeList operandList = new BaseAstNodeList();

        if (children.size() > 0) {
            for (SqlNode sqlNode : children) {
                //System.out.println("OPERAND CHILD .....   " + operandList.size());
                if (sqlNode == null) {
                    System.out.println("SQL Node is NULL..");
                } else {
                    BaseAstNode node = sqlNode.accept(this);
                    //System.out.println("Operand  - "   + " -     " + node.toString() + "   " + node.getClass());
                    operandList.add(node);
                }
            }

            //System.out.println("operandList ____________   " + operandList);
            builder.setOperands(operandList);
        }

        return builder.build();
    }


    private BaseAstNode extract(SqlNode parent, BaseAstNode.Builder builder,
                                List<SqlNode> children) {
        System.out.println(parent.getKind() + " ,  _  " + builder.toString()  + " ,  _  Children _ " + children.size());

        if (children.size() > 0) {
            int i = 0;
            for (SqlNode sqlNode : children) {

                System.out.println("LABEL      " + sqlNode.getClass());

                if (sqlNode == null) {
                    System.out.println("SQL Node is NULL..");
                } else {
                    sqlNode.accept(this);
                }
                ++ i;
            }
        }

        return builder.build();
    }


    //------------------------------------END--------------------------------------------


    @Override public BaseAstNode visit(SqlLiteral literal) {
        System.out.println("literal  " + literal.getValue() + "   " + literal.getClass());
        return new LiteralNode(literal.getValue());
    }

    @Override public BaseAstNode visit(SqlCall call) {
        System.out.println("call  " + call.getKind() + "   " + call.getOperator().getClass());
        BaseAstNode node = null;

        switch(call.getKind()) {
            case SELECT:
                SelectNode.SelectBuilder selectBuilder = new SelectNode.SelectBuilder();
                node = extractSelect(call, selectBuilder, call.getOperandList());
                break;
            case AS:
                AsNode.AsBuilder asBuilder = new AsNode.AsBuilder();
                node = extract(call, asBuilder, call.getOperandList());
                break;
            default:
                SqlOperator operator = call.getOperator();

                OperatorNode.OperatorBuilder operatorBuilder = new OperatorNode.OperatorBuilder();

                if(operator instanceof SqlBinaryOperator) {
                    node = extractOperator(operator, operatorBuilder, call.getOperandList());
                }
        }

        return node;
    }

    @Override public BaseAstNode visit(SqlNodeList nodeList) {
        System.out.println("List  " + nodeList.getKind() + "   " + nodeList.getClass());
        return extract(nodeList, new BaseAstNodeList.NodeListBuilder(), nodeList.getList());
    }

    @Override public BaseAstNode visit(SqlIdentifier id) {
        System.out.println("ID  " + id.getKind() + "   " + id.toString());
        return new IdentifierNode(id.toString());
    }

    @Override public BaseAstNode visit(SqlDataTypeSpec type) {
        System.out.println("type  " + type.getKind() + "   " + type.getClass());
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

    @Override
    public String toString() {
        return sb.toString();
    }

}
