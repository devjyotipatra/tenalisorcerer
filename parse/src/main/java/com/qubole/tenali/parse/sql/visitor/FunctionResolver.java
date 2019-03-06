package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.datamodel.*;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

public class FunctionResolver extends OperatorResolver {

    public FunctionResolver(List<Triple<String, String, List<String>>> catalog,
                            Map<String, Object> columnAliasMap) {
        super(catalog, columnAliasMap);
    }

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        FunctionNode function = (FunctionNode) node;

        String functionName = function.functionName;

        TenaliAstNodeList operands = new TenaliAstNodeList();
        int literalOperandCount = 0;
        for (TenaliAstNode nn : function.arguments) {
            if(nn instanceof AsNode) {
                nn = ((AsNode) nn).value;
            }

            if(nn instanceof LiteralNode) {
                literalOperandCount++;
            } else {
                operands.add(visitNode(nn));
            }
        }

        if(literalOperandCount > 0) {
            operands.add(new LiteralNode("TENALI_LITERAL"));
        }

        return new FunctionNode(functionName, operands);
    }
}
