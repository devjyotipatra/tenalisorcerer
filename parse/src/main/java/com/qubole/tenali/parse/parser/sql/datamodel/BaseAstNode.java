package com.qubole.tenali.parse.parser.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

import java.util.Collections;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectNode.class, name = "select"),
        @JsonSubTypes.Type(value = TableNode.class, name = "table"),
        @JsonSubTypes.Type(value = DDLNode.class, name = "ddl"),
        @JsonSubTypes.Type(value = JoinNode.class, name = "join"),
        @JsonSubTypes.Type(value = OperatorNode.class, name = "expression"),
        @JsonSubTypes.Type(value = ColumnNode.class, name = "column"),
        @JsonSubTypes.Type(value = LiteralNode.class, name = "literal"),
        @JsonSubTypes.Type(value = ErrorNode.class, name = "error")})
public abstract class BaseAstNode implements Cloneable {

    public abstract void accept(BaseAstNodeVisitor visitor);

    public List<BaseAstNode> getOperandlist() {
        return Collections.EMPTY_LIST;
    }
}
