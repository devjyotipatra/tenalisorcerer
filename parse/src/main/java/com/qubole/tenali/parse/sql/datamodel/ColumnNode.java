package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class ColumnNode extends TenaliAstNode {

    public String alias;
    public TenaliAstNode columnName;

    @JsonCreator
    public ColumnNode(@JsonProperty("alias") String alias,
                     @JsonProperty("columnName") TenaliAstNode columnName) {
        super();
        this.alias = alias;
        this.columnName = columnName;

    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(columnName);
    }

    public static class ColumnBuilder implements Builder<TenaliAstNode> {
        public String alias;
        public TenaliAstNode columnName;

        public TenaliAstNode build() {
            return new AsNode(alias, columnName);
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public TenaliAstNode getColumnName() {
            return columnName;
        }

        public void setColumnName(TenaliAstNode tableName) {
            this.columnName = tableName;
        }
    }
}