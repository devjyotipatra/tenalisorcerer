package com.qubole.tenali.parse.catalog;


import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents table in a catalog
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CatalogTable {
    private String tableName;
    private String formatInfo;
    private List<CatalogColumn> columns;
    private String viewExpandedText;

    @JsonCreator
    public CatalogTable(String tableName, String formatInfo, List<CatalogColumn> columns) {
        this.tableName = tableName;
        this.formatInfo =  formatInfo;
        this.columns = columns;
        this.viewExpandedText = null;
    }

    @JsonCreator
    public CatalogTable(String tableName, String formatInfo, List<CatalogColumn> columns,
                            String viewExpandedText) {
        this.tableName = tableName;
        this.formatInfo =  formatInfo;
        this.columns = columns;
        this.viewExpandedText = viewExpandedText;
    }


    public String getTableName() {
        return tableName;
    }

    public String getFormat() {
        return this.formatInfo;
    }


    public String getViewExpandedText() {
        return this.viewExpandedText;
    }

    public List<CatalogColumn> getColumns() {
        return this.columns;
    }

}

