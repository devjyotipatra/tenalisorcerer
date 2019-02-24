package com.qubole.tenali.parse.util;

import com.qubole.tenali.metastore.APIMetastoreClient;
import com.qubole.tenali.metastore.CachingMetastoreClient;
import com.qubole.tenali.parse.catalog.CatalogColumn;
import com.qubole.tenali.parse.catalog.CatalogSchema;
import com.qubole.tenali.parse.catalog.CatalogTable;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CachingMetastore {

    //cache ttl: 1 week.
    int TTL_MINS = 10080;

    //missingCache ttl: 1 day
    int MISSINGTTL_MINS = 1440;
    static IMetaStoreClient apimetastoreClient;

    public CachingMetastore() throws Exception {
        apimetastoreClient =
                new APIMetastoreClient(5911, "api.qubole.com",
                        "xxxxxxx");
    }


    private List<CatalogColumn> getCatalogColumns(List<FieldSchema> columns) {
        List<CatalogColumn> catalogColumns = new ArrayList();

        for(FieldSchema part : columns) {
            catalogColumns.add(new CatalogColumn(part.getName(), part.getType(), 1));
        }

        return catalogColumns;
    }


    private List<CatalogColumn> getColumns(List<FieldSchema> columns, List<FieldSchema> partitions) {
        List<CatalogColumn> schemaCols = new ArrayList();

        schemaCols.addAll(getCatalogColumns(columns));
        schemaCols.addAll(getCatalogColumns(partitions));
        return schemaCols;
    }


    public CatalogTable getSchema(String accountid, String dbName, String tableName) throws Exception {
        /*CachingMetastoreClient metastoreClient = new CachingMetastoreClient(
                "mojave-redis.9qcbtf.0001.use1.cache.amazonaws.com",
                accountid, TTL_MINS, apimetastoreClient,
                MISSINGTTL_MINS, true);*/

        Table tableInfo = apimetastoreClient.getTable(dbName, tableName);
        List<FieldSchema> cols = tableInfo.getSd().getCols();
        List<FieldSchema> partitions = tableInfo.getPartitionKeys();
        List<CatalogColumn> columns = getColumns(cols, partitions);

        return new CatalogTable(dbName + '.' + tableName, tableInfo.getSd().getOutputFormat(), columns,
                tableInfo.getViewExpandedText());
    }
}
