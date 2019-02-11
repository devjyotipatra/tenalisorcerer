package com.qubole.tenali.parse.sql.alias;

import com.qubole.tenali.parse.sql.datamodel.IdentifierNode;
import com.qubole.tenali.parse.sql.datamodel.SelectNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.parse.util.CachingMetastore;


public class CatalogResolver extends TenaliAstAliasResolver {

    String scopedDB = "default";

    CachingMetastore metastore = null;

    public CatalogResolver(String scopedDB) throws Exception{
        this.scopedDB = scopedDB;
        metastore = new CachingMetastore();
    }

    public TenaliAstNode visit(TenaliAstNode root) {
        TenaliAstNode node = null;

        System.out.println("CatalogResolver => " );

        if(root instanceof IdentifierNode) {
            String tableName = ((IdentifierNode) root).name;
            System.out.println("tableName => " + tableName);

            String dbName = scopedDB;
            String tabName = null;

            if(tableName.contains(".")) {
                String[] tokens = tableName.split("\\.");
                dbName = tokens[0];
                tabName = tokens[1];
            } else {
                tabName = tableName;
            }

            try {
                System.out.println(metastore.getCatalog("5911", dbName, tabName));
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        return node;
    }

}
