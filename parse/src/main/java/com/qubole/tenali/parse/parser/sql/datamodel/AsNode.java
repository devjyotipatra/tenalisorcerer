package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class AsNode extends BaseAstNode {

    public String aliasName;
    public Object value;

    @JsonCreator
    public AsNode(@JsonProperty("aliasName") String aliasName,
                        @JsonProperty("value") Object value) {
        this.aliasName = aliasName;
        this.value = value;

    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }

    public static class AsBuilder implements Builder {
        public String aliasName;
        public Object value;

        public BaseAstNode build() {
            return new AsNode(aliasName, value);
        }

        public String getAliasName() {
            return aliasName;
        }

        public void setAliasName(String aliasName) {
            this.aliasName = aliasName;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}