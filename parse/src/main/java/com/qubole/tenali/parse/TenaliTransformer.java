package com.qubole.tenali.parse;


public abstract class TenaliTransformer<S, T> implements TenaliBaseVisitor<S, T> {
    public String defaultDb = "default";

    private final Class<?> type;

    protected TenaliTransformer(Class<?> type) {
        this.type = type;
    }


    public Class getType() {
        return type;
    }

    public String getIdentifier() {
        return getClass().getCanonicalName();
    }

}
