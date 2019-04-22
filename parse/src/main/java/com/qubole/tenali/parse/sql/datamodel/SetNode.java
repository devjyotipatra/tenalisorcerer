package com.qubole.tenali.parse.sql.datamodel;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;


public class SetNode extends TenaliAstNode {

    public String key;
    public String value;

    @JsonCreator
    public SetNode(@JsonProperty("key") String key, @JsonProperty("value") String value) {
        super();
        this.key = key;
        this.value = value;
    }

    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        return key + "." + value;
    }

    @Override
    public String toString() {
        return key + "." + value;
    }

}