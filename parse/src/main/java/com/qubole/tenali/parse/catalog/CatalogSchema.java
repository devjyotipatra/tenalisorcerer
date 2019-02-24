package com.qubole.tenali.parse.catalog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Represents Schemas in a Catalog where every
 * Schema is a collection of tables.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogSchema {
    public Map<String, List<CatalogTable>> schema;

    @JsonCreator
    public CatalogSchema(@JsonProperty("schema") Map<String, List<CatalogTable>> schema) {
        this.schema = schema;
    }
}