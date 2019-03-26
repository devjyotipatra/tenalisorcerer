package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.*;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

public class OperatorResolver extends TenaliAstBaseVisitor<TenaliAstNode> {

    List<Triple<String, String, List<String>>> catalog;

    Map<String, Object> columnAliasMap;

    public OperatorResolver(List<Triple<String, String, List<String>>> catalog,
                            Map<String, Object> columnAliasMap) {
        this.catalog = catalog;
        this.columnAliasMap = columnAliasMap;
    }

    @Override
    public TenaliAstNode visit(TenaliAstNode node) {
        OperatorNode operator = (OperatorNode) node;

        String operatorName = operator.operator;

        TenaliAstNodeList operands = new TenaliAstNodeList();
        for (TenaliAstNode nn : operator.operands) {
            TenaliAstNode tn = visitNode(nn);

            /*if(tn instanceof TenaliAstNodeList) {
                tn = ((TenaliAstNodeList) tn).getOperandlist().get(0);
            }*/

            operands.add(tn);
        }

        return new OperatorNode(operatorName, operands);
    }


    protected TenaliAstNode visitNode(TenaliAstNode node) {
        if(node instanceof LiteralNode) {
            return (TenaliAstNode) node.accept(new LiteralRedactor());
        } else if(node instanceof FunctionNode) {
            return (TenaliAstNode) node.accept(new FunctionResolver(catalog, columnAliasMap));
        } else if(node instanceof OperatorNode) {
            return (TenaliAstNode) node.accept(new OperatorResolver(catalog, columnAliasMap));
        }

        return resolveColumns(node, catalog, columnAliasMap);
    }

}
