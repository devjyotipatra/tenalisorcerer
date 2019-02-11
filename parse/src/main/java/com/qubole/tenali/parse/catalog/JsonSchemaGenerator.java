package com.qubole.tenali.parse.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class JsonSchemaGenerator {
    private boolean isFileReader = false;

    public JsonSchemaGenerator() {}

    public JsonSchemaGenerator(boolean isFileReader) {
        this.isFileReader = isFileReader;
    }

    public CatalogSchema convert(String obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        if (isFileReader) {
            File file = new File(obj);
            return mapper.readValue(file, CatalogSchema.class);
        } else {
            return mapper.readValue(obj, CatalogSchema.class);
        }
    }
}
