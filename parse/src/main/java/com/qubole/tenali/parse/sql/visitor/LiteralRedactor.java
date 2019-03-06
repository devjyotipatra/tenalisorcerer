package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.IdentifierNode;
import com.qubole.tenali.parse.sql.datamodel.LiteralNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

public class LiteralRedactor extends TenaliAstBaseVisitor<TenaliAstNode> {

    public TenaliAstNode visit(TenaliAstNode node) {
        System.out.println("INSIDE  Literal Redactor ## " + node.getClass());
        if(node instanceof LiteralNode) {
            return new LiteralNode("TENALI_LITERAL");
        }

        return node;
    }
}
