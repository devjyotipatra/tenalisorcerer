package com.qubole.tenali.parse.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

public class MetaNode extends TenaliAstNode {

    public String statement;

    @JsonCreator
    public MetaNode(@JsonProperty("statement") String statement) {
        super();
        this.statement = statement;

    }

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return statement;
    }

    @Override
    public String toString() {
        return statement;
    }

}