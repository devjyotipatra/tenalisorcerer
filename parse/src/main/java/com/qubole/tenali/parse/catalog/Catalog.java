package com.qubole.tenali.parse.catalog;

public interface Catalog {

    public CatalogTable getSchema(String schema, String table) throws Exception;
}
