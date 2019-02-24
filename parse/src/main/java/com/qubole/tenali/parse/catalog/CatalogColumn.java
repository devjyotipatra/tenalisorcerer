package com.qubole.tenali.parse.catalog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a column in a Catalog
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CatalogColumn {
    private String name;
    private String type;
    private Integer isPartition;

    public String getType() {
        return type;
    }

    public Integer getIsPartition() {
        return isPartition;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public CatalogColumn(@JsonProperty("name") String name,
                         @JsonProperty("type") String type,
                         @JsonProperty("is_partition") int isPartition) {
        this.name = name;
        this.type = type;
        this.isPartition = isPartition;
    }
}
