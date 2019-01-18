package com.qubole.tenali.parse.parser.util;

import com.qubole.tenali.metastore.APIMetastoreClient;
import com.qubole.tenali.metastore.CachingMetastoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import java.util.List;

public class CachingMetastore {

    //cache ttl: 1 week.
    int TTL_MINS = 10080;

    //missingCache ttl: 1 day
    int MISSINGTTL_MINS = 1440;
    static IMetaStoreClient apimetastoreClient;

    public CachingMetastore() throws Exception {
        IMetaStoreClient apimetastoreClient =
                new APIMetastoreClient(5911, "api.qubole.com",
                        "EMPTY");
    }

    public List<FieldSchema> getCatalog(String accountid, String dbName, String tableName) throws Exception {
        CachingMetastoreClient metastoreClient = new CachingMetastoreClient(
                "mojave-redis.9qcbtf.0001.use1.cache.amazonaws.com",
                accountid, TTL_MINS, apimetastoreClient,
                MISSINGTTL_MINS, true);

        Table tableInfo = metastoreClient.getTable(dbName, tableName);
        return tableInfo.getSd().getCols();
    }
}
