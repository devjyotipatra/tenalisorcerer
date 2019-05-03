package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;


public class DDLNode extends TenaliAstNode {
    public String ddlToken;
    public TenaliAstNode selectNode;
    public IdentifierNode tableNode;

    @JsonCreator
    public DDLNode(@JsonProperty("ddlToken") String token,
                   @JsonProperty("selectNode") TenaliAstNode selectNode,
                   @JsonProperty("tableNode") IdentifierNode tableNode) {
        this.ddlToken = token;
        this.selectNode = selectNode;
        this.tableNode = tableNode;
    }

    public TenaliAstNode getSelectNode() {
        return selectNode;
    }

    public IdentifierNode getTableNode() {
        return tableNode;
    }

    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        if(selectNode != null) {
            return visitor.visit(selectNode);
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ddlToken).append(" == ");
        if(selectNode != null) {
            sb.append(selectNode.toString()).append(" == ");
        }

        sb.append(tableNode.toString());

        return sb.toString();
    }
}