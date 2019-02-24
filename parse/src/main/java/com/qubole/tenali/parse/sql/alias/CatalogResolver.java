package com.qubole.tenali.parse.sql.alias;

import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.catalog.CatalogTable;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.IdentifierNode;
import com.qubole.tenali.parse.sql.datamodel.SelectNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.parse.util.CachingMetastore;

import java.util.Collections;


public class CatalogResolver extends TenaliAstBaseVisitor<CatalogTable> {

    CachingMetastore metastore = null;

    public CatalogResolver() throws Exception{
        metastore = new CachingMetastore();
    }

    public CatalogTable visit(TenaliAstNode root) {
        CatalogTable catalog = null;

        if(root instanceof IdentifierNode) {
            String tableName = ((IdentifierNode) root).name;
            System.out.println("tableName => " + tableName);

            String dbName = defaultDb;
            String tabName;

            if(tableName.contains(".")) {
                String[] tokens = tableName.split("\\.");
                dbName = tokens[0];
                tabName = tokens[1];
            } else {
                tabName = tableName;
            }

            try {
                catalog = metastore.getSchema("5911", dbName, tabName);
            } catch(Exception ex) {
                System.out.println(ex.getMessage() + "  " + ex.toString());
                catalog = new CatalogTable(tableName, null, Collections.EMPTY_LIST);
            }
        }

        return catalog;
    }




}


//[FieldSchema(name:id, type:int, comment:null), FieldSchema(name:qbol_user_id, type:int, comment:null), FieldSchema(name:submit_time, type:int, comment:null), FieldSchema(name:end_time, type:int, comment:null), FieldSchema(name:progress, type:int, comment:null),
// FieldSchema(name:cube_id, type:int, comment:null), FieldSchema(name:updated_at, type:string, comment:null), FieldSchema(name:created_at, type:string, comment:null), FieldSchema(name:path, type:string, comment:null), FieldSchema(name:status, type:varchar(255), comment:null),
// FieldSchema(name:host_name, type:varchar(255), comment:null), FieldSchema(name:user_loc, type:boolean, comment:null), FieldSchema(name:qbol_session_id, type:int, comment:null), FieldSchema(name:command_id, type:int, comment:null),
// FieldSchema(name:command_type, type:varchar(255), comment:null), FieldSchema(name:qlog, type:string, comment:null), FieldSchema(name:periodic_job_id, type:int, comment:null), FieldSchema(name:wf_id, type:string, comment:null), FieldSchema(name:command_source, type:string, comment:null),
// FieldSchema(name:resolved_macros, type:string, comment:null), FieldSchema(name:status_code, type:int, comment:null), FieldSchema(name:pid, type:int, comment:null), FieldSchema(name:command_template_id, type:int, comment:null), FieldSchema(name:command_template_mutable_id, type:int, comment:null),
// FieldSchema(name:editable_pj_id, type:int, comment:null), FieldSchema(name:template, type:varchar(255), comment:null), FieldSchema(name:can_notify, type:boolean, comment:null), FieldSchema(name:num_result_dir, type:int, comment:null), FieldSchema(name:start_time, type:int, comment:null),
// FieldSchema(name:pool, type:varchar(255), comment:null), FieldSchema(name:timeout, type:int, comment:null), FieldSchema(name:tag, type:varchar(255), comment:null), FieldSchema(name:name, type:varchar(255), comment:null), FieldSchema(name:saved_query_mutable_id, type:int, comment:null),
// FieldSchema(name:account_id, type:int, comment:null)]