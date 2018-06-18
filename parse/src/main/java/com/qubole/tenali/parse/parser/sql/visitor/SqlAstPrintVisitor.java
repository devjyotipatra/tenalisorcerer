package com.qubole.tenali.parse.parser.sql.visitor;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.util.SqlVisitor;

import static java.util.Arrays.asList;
import java.util.List;


/**
 * Visitor to print as a tree
 */
public class SqlAstPrintVisitor implements SqlVisitor<Void> {
    private StringBuilder sb = new StringBuilder();
    private int indent = 0;

    public static String toSQLString(SqlNode sqlNode) {
        SqlPrettyWriter writer = new SqlPrettyWriter(SqlDialect.CALCITE);
        writer.setSelectListItemsOnSeparateLines(false);
        writer.setIndentation(0);
        writer.setQuoteAllIdentifiers(false);
        sqlNode.unparse(writer, 0, 0);
        return writer.toString();
    }

    private Void format(SqlNode parent) {
        return format(parent, toSQLString(parent));
    }

    private Void format(SqlNode parent, String desc, SqlNode... nodes) {
        return format(parent, desc, asList(nodes));
    }

    private String label(SqlKind kind, int i) {
        switch (kind) {
            case SELECT:
                switch(i)  {
                    case 0:
                        return "keywords";
                    case 1:
                        return "select";
                    case 2:
                        return "from";
                    case 3:
                        return "where";
                    case 4:
                        return "groupBy";
                    case 5:
                        return "having";
                    case 6:
                        return "windowDecls";
                    case 7:
                        return "orderBy";
                    case 8:
                        return "offset";
                    case 9:
                        return "fetch";
                    default:
                }
            case ORDER_BY:
                switch(i) {
                    case 0:
                        return "query";
                    case 1:
                        return "orderList";
                    case 2:
                        return "offset";
                    case 3:
                        return "fetch";
                    default:
                }
            default:
        }
        return String.valueOf(i);
    }

    private Void format(SqlNode parent, String desc, List<SqlNode> children) {
        sb.append(parent.getKind()).append(": ").append(desc);
        if (children.size() > 0) {
            sb.append(" {\n");
            indent += 1;
            int i = 0;
            for (SqlNode sqlNode : children) {
                indent();
                sb.append(label(parent.getKind(), i)).append(": ");
                if (sqlNode == null) {
                    sb.append("null");
                } else {
                    sqlNode.accept(this);
                }
                sb.append(",\n");
                ++ i;
            }
            indent();
            sb.append("}");
            indent -= 1;
        }
        return null;
    }

    private void indent() {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }

    @Override public Void visit(SqlLiteral literal) {
        System.out.println("Svisit(SqlLiteral literal)");
        return format(literal);
    }

    @Override public Void visit(SqlCall call) {
        System.out.println("visit(SqlCall call)");
        return format(call, call.getOperator().toString(), call.getOperandList());
    }

    @Override public Void visit(SqlNodeList nodeList) {
        System.out.println("visit(SqlNodeList nodeList)");
        return format(nodeList, "list", nodeList.getList());
    }

    @Override public Void visit(SqlIdentifier id) {
        System.out.println("visit(SqlIdentifier id)");
        return format(id);
    }

    @Override public Void visit(SqlDataTypeSpec type) {
        System.out.println("visit(SqlDataTypeSpec type)");
        return format(type);
    }

    @Override public Void visit(SqlDynamicParam param) {
        System.out.println("visit(SqlDynamicParam param)");
        return format(param);
    }

    @Override public Void visit(SqlIntervalQualifier intervalQualifier) {
        System.out.println("visit(SqlIntervalQualifier intervalQualifier)");
        return format(intervalQualifier);
    }

    @Override
    public String toString() {
        return sb.toString();
    }

}