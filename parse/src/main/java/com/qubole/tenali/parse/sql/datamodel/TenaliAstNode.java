package com.qubole.tenali.parse.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectNode.class, name = "select"),
        @JsonSubTypes.Type(value = DDLNode.class, name = "ddl"),
        @JsonSubTypes.Type(value = FunctionNode.class, name = "function"),
        @JsonSubTypes.Type(value = IdentifierNode.class, name = "identifier"),
        @JsonSubTypes.Type(value = JoinNode.class, name = "join"),
        @JsonSubTypes.Type(value = LateralNode.class, name = "lateral"),
        @JsonSubTypes.Type(value = OperatorNode.class, name = "operator"),
        @JsonSubTypes.Type(value = AsNode.class, name = "as"),
        @JsonSubTypes.Type(value = LiteralNode.class, name = "literal"),
        @JsonSubTypes.Type(value = TenaliAstNodeList.class, name = "list"),
        @JsonSubTypes.Type(value = ErrorNode.class, name = "error")})
public abstract class TenaliAstNode implements Cloneable {

    public abstract Object accept(TenaliAstBaseVisitor visitor);


    public interface Builder<T> {
        public T build();

        public String toString();
    }
}
