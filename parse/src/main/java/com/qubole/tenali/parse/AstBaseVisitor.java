package com.qubole.tenali.parse;

import java.lang.reflect.Type;


public abstract class AstBaseVisitor<S, T> implements AstTransformer<S, T> {
    private final Class<S> type;

    protected AstBaseVisitor(Class<S> type) {
        this.type = type;
    }


    public Class getType() {
        return type;
    }

    public String getIdentifier() {
        return getClass().getCanonicalName();
    }

}
