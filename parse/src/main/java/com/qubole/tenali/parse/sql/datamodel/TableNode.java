package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class TableNode extends TenaliAstNode {

    public String alias;
    public TenaliAstNode tableName;

    @JsonCreator
    public TableNode(@JsonProperty("alias") String alias,
                  @JsonProperty("tableName") TenaliAstNode tableName) {
        super();
        this.alias = alias;
        this.tableName = tableName;

    }

    @Override
    public void accept(TenaliAstBaseVisitor visitor) {
        return;
    }

    public static class TableBuilder implements Builder<TenaliAstNode> {
        public String alias;
        public TenaliAstNode tableName;

        public TenaliAstNode build() {
            return new AsNode(alias, tableName);
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public TenaliAstNode getTableName() {
            return tableName;
        }

        public void setTableName(TenaliAstNode tableName) {
            this.tableName = tableName;
        }
    }
}