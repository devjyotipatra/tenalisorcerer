package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.*;

public class PrintVisitor extends TenaliAstBaseVisitor<String> {

    StringBuilder sb = new StringBuilder();

    public String visit(TenaliAstNode node) {
        if(node instanceof LiteralNode) {
            sb.append(((LiteralNode) node).value).append(" ");
        } else if (node instanceof IdentifierNode) {
            sb.append(((IdentifierNode) node).name).append(" ");
        } else if(node instanceof OperatorNode) {
            OperatorNode on = (OperatorNode) node;
            sb.append("\nOPER_" + on.operator).append("\n");
            for(TenaliAstNode nn : on.operands) {
                nn.accept(this);
            }
        } else if(node instanceof FunctionNode) {
            FunctionNode fn = (FunctionNode) node;
            sb.append("\nFUNC_" + fn.functionName).append("\n");;
            for(TenaliAstNode nn : fn.arguments) {
                nn.accept(this);
            }
        } else if(node instanceof SelectNode) {
            SelectNode sn = (SelectNode) node;
            sb.append("\nFROM \n");
            sn.from.accept(this);
            if(sn.where != null) {
                sb.append("\nWHERE \n");
                sn.where.accept(this);
            }
            //sn.orderBy.accept(this);
            //sn.groupBy.accept(this);
            //sn.with.accept(this);
            sb.append("\nCOLUMNS \n");
            sn.columns.accept(this);
            //sn.keywords.accept(this);
            //sn.having.accept(this);
        } else if(node instanceof TenaliAstNodeList) {
            for(TenaliAstNode nn : ((TenaliAstNodeList) node).getOperandlist()) {
                nn.accept(this);
            }
        } else if(node instanceof JoinNode) {
            JoinNode jn = (JoinNode) node;
            sb.append("\njoin \n");
            jn.leftNode.accept(this);
            jn.joinCondition.accept(this);
            jn.rightNode.accept(this);
        }

        return sb.toString();
    }
}