package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class LateralNode extends BaseAstNode {

    public final BaseAstNode table;

    @JsonCreator
    public LateralNode(@JsonProperty("table") BaseAstNode table) {
        super();
        this.table = table;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }

    public static class LateralBuilder implements Builder {
        BaseAstNode table;

        public BaseAstNode build() {
            return new LateralNode(table);
        }

        public BaseAstNode getTable() {
            return table;
        }

        public void setTable(BaseAstNode table) {
            this.table = table;
        }
    }
}