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

    private void addTable(List<String> tables1, List<String> tables2) {
        if(tables2 != null) {
            tables1.addAll(tables2);
        }
    }


    public List<String> visit(TenaliAstNode root) {
        List<String> tables = new ArrayList();

        try {
            if (root instanceof SelectNode) {
                addTable(tables, visitSelectNode(root));
            } else if (root instanceof IdentifierNode) {
                tables.add(((IdentifierNode) root).name);
            } else if(root instanceof DDLNode) {
                DDLNode ddl = (DDLNode) root;
                tables.add(ddl.tableNode.name);

                if(ddl.selectNode != null) {
                    addTable(tables, visitSelectNode(((DDLNode) root).selectNode));
                }
            } else if (root instanceof JoinNode) {
                JoinNode join = (JoinNode) root;
                Object[] obj = (Object []) join.accept(this);
                if(obj.length == 2) {
                    addTable(tables, (List<String>) obj[0]);
                    addTable(tables, (List<String>) obj[1]);
                }
            } else  if (root instanceof TenaliAstNodeList) {
                TenaliAstNodeList ast = (TenaliAstNodeList) root;
                for(int i=0; i<ast.size(); i++ ) {
                    addTable(tables, (List<String>) ast.accept(this));
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
