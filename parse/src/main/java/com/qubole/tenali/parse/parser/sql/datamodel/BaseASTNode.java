package com.qubole.tenali.parse.parser.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.qubole.tenali.parse.parser.sql.visitor.BaseASTNodeVisitor;

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
        @JsonSubTypes.Type(value = AsNode.class, name = "as"),
        @JsonSubTypes.Type(value = UnsupportedNode.class, name = "unsupported"),
        @JsonSubTypes.Type(value = JoinNode.class, name = "join"),
        @JsonSubTypes.Type(value = ErrorNode.class, name = "error"),
        @JsonSubTypes.Type(value = OperatorNode.class, name = "expression"),
        @JsonSubTypes.Type(value = IdentifierNode.class, name = "identifier"),
        @JsonSubTypes.Type(value = LiteralNode.class, name = "literal")})
public abstract class BaseASTNode implements Cloneable {
    public ASTNodeType getKind() {
        return ASTNodeType.OTHER;
    }

    public abstract void accept(BaseASTNodeVisitor visitor);

    public List<BaseASTNode> getOperandlist() {
        return Collections.EMPTY_LIST;
    }
}
