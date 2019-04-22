package com.qubole.tenali.parse;


public abstract class TenaliTransformer<S, T> implements TenaliBaseVisitor<S, T> {
    private final Class<S> type;

    protected TenaliTransformer(Class<S> type) {
        this.type = type;
    }


    public Class getType() {
        return type;
    }

    public String getIdentifier() {
        return getClass().getCanonicalName();
    }

}
