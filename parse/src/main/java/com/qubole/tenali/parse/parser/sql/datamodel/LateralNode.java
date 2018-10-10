package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.TenaliAstNodeVisitor;

public class LateralNode extends TenaliAstNode {

    public final TenaliAstNode table;

    @JsonCreator
    public LateralNode(@JsonProperty("table") TenaliAstNode table) {
        super();
        this.table = table;
    }

    @Override
    public void accept(TenaliAstNodeVisitor visitor) {
        return;
    }

    public static class LateralBuilder implements Builder<TenaliAstNode> {
        TenaliAstNode table;

        public TenaliAstNode build() {
            return new LateralNode(table);
        }

        public TenaliAstNode getTable() {
            return table;
        }

        public void setTable(TenaliAstNode table) {
            this.table = table;
        }
    }
}