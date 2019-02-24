package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class AsNode extends TenaliAstNode {

    public String aliasName;
    public TenaliAstNode value;

    @JsonCreator
    public AsNode(@JsonProperty("aliasName") String aliasName,
                        @JsonProperty("value") TenaliAstNode value) {
        super();
        this.aliasName = aliasName;
        this.value = value;

    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return value.accept(visitor);
    }

    public static class AsBuilder implements Builder<TenaliAstNode> {
        public String aliasName;
        public TenaliAstNode value;

        public AsBuilder() {}

        public AsBuilder(AsNode asNode) {
            this.aliasName = asNode.aliasName;
            this.value = asNode.value;
        }

        public TenaliAstNode build() {
            return new AsNode(aliasName, value);
        }

        public String getAliasName() {
            return aliasName;
        }

        public void setAliasName(String aliasName) {
            this.aliasName = aliasName;
        }

        public TenaliAstNode getValue() {
            return value;
        }

        public void setValue(TenaliAstNode value) {
            this.value = value;
        }
    }
}