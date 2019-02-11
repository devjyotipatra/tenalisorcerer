package com.qubole.tenali.parse;

import com.qubole.tenali.parse.util.CachingMetastore;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.junit.Test;

import java.util.List;

public class CachingMetastoreClientTest {


    @Test
    public void testSimpleSelectQuery() throws Exception {
        String accountid = "5911";
        String dbName = "rstore";
        String tableName = "query_hists";

        CachingMetastore client = new CachingMetastore();
        List<FieldSchema> columnList = client.getCatalog(accountid, dbName, tableName);

        for(FieldSchema field : columnList) {
            System.out.println("=======> " + field.getName());
        }
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}
