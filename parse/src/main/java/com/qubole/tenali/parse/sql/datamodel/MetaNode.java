package com.qubole.tenali.parse.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;

public class MetaNode extends TenaliAstNode {

    public String metaType;

    public String statement;

    @JsonCreator
    public MetaNode(@JsonProperty("meta_type") String type,
                    @JsonProperty("statement") String statement) {
        super();
        this.metaType = type;
        this.statement = statement;

    }

    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        return statement;
    }

    @Override
    public String toString() {
        return statement;
    }

}