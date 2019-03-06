package com.qubole.tenali.parse.util;

import com.qubole.tenali.metastore.APIMetastoreClient;
import com.qubole.tenali.metastore.CachingMetastoreClient;
import com.qubole.tenali.parse.catalog.Catalog;
import com.qubole.tenali.parse.catalog.CatalogColumn;
import com.qubole.tenali.parse.catalog.CatalogTable;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import java.util.ArrayList;
import java.util.List;


public class CachingMetastore implements Catalog {

    //cache ttl: 1 week.
    int TTL_MINS = 10080;

    //missingCache ttl: 1 day
    int MISSINGTTL_MINS = 1440;

    static IMetaStoreClient metastoreClient;

    public CachingMetastore(int accountId, String env, String authToken, String cachingServer) throws Exception {
        APIMetastoreClient apiMetastoreClient = new APIMetastoreClient(accountId, env, authToken);
                //new APIMetastoreClient(accountId, env, authToken);

        /*metastoreClient = new CachingMetastoreClient(
                //cachingServer,
                cachingServer,
                String.valueOf(accountId),
                TTL_MINS,
                apiMetastoreClient,
                MISSINGTTL_MINS,
                true);*/

        metastoreClient = apiMetastoreClient;
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


    public CatalogTable getSchema(String dbName, String tableName) throws Exception {
        Table tableInfo = metastoreClient.getTable(dbName, tableName);
        List<FieldSchema> cols = tableInfo.getSd().getCols();
        List<FieldSchema> partitions = tableInfo.getPartitionKeys();
        List<CatalogColumn> columns = getColumns(cols, partitions);

        return new CatalogTable(dbName + '.' + tableName, tableInfo.getSd().getOutputFormat(), columns,
                tableInfo.getViewExpandedText());
    }
}
