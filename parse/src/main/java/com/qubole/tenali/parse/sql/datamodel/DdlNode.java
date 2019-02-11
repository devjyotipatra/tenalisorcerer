package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class DdlNode extends TenaliAstNode {
    public final TenaliAstNode tableNode;
    public final TenaliAstNode ctasNode;
    public final TenaliAstNode alterNode;
    @JsonCreator
    public DdlNode(@JsonProperty("table") TenaliAstNode tableNode,
                   @JsonProperty("ctas") TenaliAstNode ctasNode,
                   @JsonProperty("alter") TenaliAstNode alterNode) {
        super();
        this.tableNode = tableNode;
        this.ctasNode = ctasNode;
        this.alterNode = alterNode;
    }

    @Override
    public void accept(TenaliAstBaseVisitor visitor) {
        visitor.visit(this);
    }
}
