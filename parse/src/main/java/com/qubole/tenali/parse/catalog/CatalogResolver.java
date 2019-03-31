package com.qubole.tenali.parse.catalog;

import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.IdentifierNode;
import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;


public class CatalogResolver<T extends Catalog> extends TenaliAstBaseVisitor<CatalogTable> {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogResolver.class);

    final T catalog;

    public CatalogResolver(T catalog) throws Exception{
        assert(catalog != null);
        this.catalog = catalog;
    }

    public CatalogTable visit(TenaliAstNode root) {
        CatalogTable catalogTable = null;

        if(root instanceof IdentifierNode) {
            String tableName = ((IdentifierNode) root).name;

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
                LOG.info(String.format("Calling Metastore API for resolving  %s.%s ", dbName, tabName));
                catalogTable = catalog.getSchema(dbName.toLowerCase(), tabName.toLowerCase());
            } catch(Exception ex) {
                LOG.error(ex.getMessage() + "  " + ex.toString());
                catalogTable = new CatalogTable(tableName, null, Collections.EMPTY_LIST);
            }
        }

        return catalogTable;
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