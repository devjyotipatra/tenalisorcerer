package com.qubole.tenali.parse.sql.visitor;

import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;
import com.qubole.tenali.parse.sql.datamodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableExtractorVisitor extends TenaliAstBaseTransformer<List<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(TableExtractorVisitor.class);

    private List<String> extractFromAstNodeList(Object[] arr) {
        List<String> tables = new ArrayList();
        for(int i=0; i<arr.length; i++) {
            if(arr[i] != null) {
                tables.addAll((List<String>) arr[i]);
            }
        }

        return tables;
    }


    public List<String> visit(TenaliAstNode root) {
        List<String> tables = new ArrayList();

        try {
            if (root instanceof SelectNode) {
                List<String> tab = visitSelectNode(root);
                if(tab != null) {
                    tables.addAll(tab);
                }
            } else if (root instanceof IdentifierNode) {
                tables.add(((IdentifierNode) root).name);
            } else if(root instanceof DDLNode) {
                DDLNode ddl = (DDLNode) root;
                tables.add(ddl.tableNode.name);
                tables.addAll((List<String>) ddl.accept(this));
            } else if (root instanceof JoinNode) {
                JoinNode join = (JoinNode) root;
                tables.addAll((List<String>) join.leftNode.accept(this));
                tables.addAll((List<String>) join.rightNode.accept(this));
            } else  {
                Object obj = root.accept(this);
                if(obj instanceof List) {
                    tables.addAll((List<String>) obj);
                } else {
                    tables.add((String) obj);
                }
            }
        } catch(Exception ex) {
            LOG.error(ex.getMessage());
            ex.printStackTrace();
        }

        return tables;
    }


    public List<String> visitSelectNode(TenaliAstNode select) {
        Set<String> tables = new HashSet();

        if(select == null || ((SelectNode) select).from == null) {
            return null;
        }

        SelectNode sNode = (SelectNode) select;

        TenaliAstNodeList with = sNode.with;
        if (with != null) {
            Object[] arr = (Object[]) with.accept(this);
            tables.addAll(extractFromAstNodeList(arr));
        }

        Object[] arr = (Object[]) sNode.from.accept(this);
        tables.addAll(extractFromAstNodeList(arr));

        return new ArrayList(tables);
    }
}
