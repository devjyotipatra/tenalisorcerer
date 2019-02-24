package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class LateralNode extends TenaliAstNode {

    public final TenaliAstNode table;

    @JsonCreator
    public LateralNode(@JsonProperty("table") TenaliAstNode table) {
        super();
        this.table = table;
    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(table);
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