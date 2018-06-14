package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class DDLNode extends BaseAstNode {
    public final BaseAstNode tableNode;
    public final BaseAstNode ctasNode;
    public final BaseAstNode alterNode;
    @JsonCreator
    public DDLNode(@JsonProperty("table") BaseAstNode tableNode,
                   @JsonProperty("ctas") BaseAstNode ctasNode,
                   @JsonProperty("alter") BaseAstNode alterNode) {
        this.tableNode = tableNode;
        this.ctasNode = ctasNode;
        this.alterNode = alterNode;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
