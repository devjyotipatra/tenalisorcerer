package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.TenaliAstBaseTransformer;


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
        System.out.println(visitor);
        if(selectNode != null) {
            return selectNode.accept(visitor);
        }

        return this;
    }
}