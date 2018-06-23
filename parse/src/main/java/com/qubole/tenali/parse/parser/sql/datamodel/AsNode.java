package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class AsNode extends BaseAstNode {

    public String aliasName;
    public BaseAstNode value;

    @JsonCreator
    public AsNode(@JsonProperty("aliasName") String aliasName,
                        @JsonProperty("value") BaseAstNode value) {
        super();
        this.aliasName = aliasName;
        this.value = value;

    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }

    public static class AsBuilder implements Builder {
        public String aliasName;
        public BaseAstNode value;

        public BaseAstNode build() {
            return new AsNode(aliasName, value);
        }

        public String getAliasName() {
            return aliasName;
        }

        public void setAliasName(String aliasName) {
            this.aliasName = aliasName;
        }

        public BaseAstNode getValue() {
            return value;
        }

        public void setValue(BaseAstNode value) {
            this.value = value;
        }
    }
}