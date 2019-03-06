package com.qubole.tenali.parse;

import com.qubole.tenali.parse.catalog.CatalogColumn;
import com.qubole.tenali.parse.catalog.CatalogTable;
import com.qubole.tenali.parse.util.CachingMetastore;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.junit.Test;

import java.util.List;

public class CachingMetastoreClientTest {


   /* @Test
    public void testSimpleSelectQuery() throws Exception {
        String accountid = "5911";
        String dbName = "rstore";
        String tableName = "query_hists";

        CachingMetastore client = new CachingMetastore();
        CatalogTable schema = client.getSchema(accountid, dbName, tableName);

        for(CatalogColumn column : schema.getColumns()) {
            System.out.println("=======> " + column.getName());
        }
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }*/
}
