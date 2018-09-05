package com.qubole.tenali.parse.parser.config;

public abstract class TenaliType<T> {

    T type;

    TenaliType(T type) {
        this.type = type;
    }

    public void setValue(T type) { this.type = type; }

    public T getValue(){ return type; }


    @Override
    public boolean equals(Object o) {
        TenaliType other = (TenaliType) o;
        return this.type.toString().equals(other.getValue().toString());
    }


    public String toString() { return type.toString(); }


}
