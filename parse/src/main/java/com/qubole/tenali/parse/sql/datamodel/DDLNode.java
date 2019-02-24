package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class DDLNode extends TenaliAstNode {
    public String ddlToken;
    public TenaliAstNode selectNode;
    public TenaliAstNode tableNode;

    @JsonCreator
    public DDLNode(@JsonProperty("ddlToken") String token,
                   @JsonProperty("selectNode") TenaliAstNode selectNode,
                   @JsonProperty("tableNode") TenaliAstNode tableNode) {
        this.ddlToken = token;
        this.selectNode = selectNode;
        this.tableNode = tableNode;
    }

    public TenaliAstNode getSelectNode() {
        return selectNode;
    }

    public TenaliAstNode getTableNode() {
        return tableNode;
    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(this);
    }
}