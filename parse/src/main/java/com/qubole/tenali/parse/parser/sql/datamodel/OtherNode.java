package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

public class OtherNode extends BaseAstNode {
    public String name;
    public Object value;
    @JsonCreator
    public OtherNode(@JsonProperty("name") String name,
                     @JsonProperty("value") Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        return;
    }

    public class OtherBuilder implements Builder {
        String name;
        String value;

        @Override
        public BaseAstNode build() {
            return new OtherNode(name, value);
        }

        @Override
        public String toString() {
            return String.format("{\"name\": \"%s\", \"value\":\"%s\"", name, value);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
