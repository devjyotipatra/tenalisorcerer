package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;
import com.qubole.tenali.parse.sql.datamodel.LiteralNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;

public class LiteralRedactor extends TenaliAstBaseTransformer<TenaliAstNode> {

    public TenaliAstNode visit(TenaliAstNode node) {
        if(node instanceof LiteralNode) {
            return new LiteralNode("TENALI_LITERAL");
        }

        return node;
    }
}
