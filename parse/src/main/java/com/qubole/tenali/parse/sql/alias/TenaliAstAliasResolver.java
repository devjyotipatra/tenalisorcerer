package com.qubole.tenali.parse.sql.alias;

import com.qubole.tenali.parse.sql.datamodel.*;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

import javax.jdo.annotations.Join;


public class TenaliAstAliasResolver extends TenaliAstBaseVisitor<TenaliAstNode> {

    String scopedDB = "default";

    public TenaliAstNode visit(TenaliAstNode root) {
        System.out.println("TenaliAstAliasResolver => " + root.getClass());

        if(root instanceof SelectNode) {
            return visitSelectNode(root);
        } else if(root instanceof JoinNode) {
            JoinNode join = ((JoinNode) root);
            join.accept(this);
        } else {
            visitFromNode(root);
        }

        return root;
    }

    public TenaliAstNode visitSelectNode(TenaliAstNode select) {
        TenaliAstNodeList columns = ((SelectNode) select).columns;
        TenaliAstNode from = ((SelectNode) select).from;
        return visitFromNode(from);
    }

    public TenaliAstNode visitFromNode(TenaliAstNode from) {
        if(from instanceof AsNode) {
            AsNode t = ((AsNode) from);
            String alias = t.aliasName;

            try {
                t.accept(new CatalogResolver(scopedDB));
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else if(from instanceof JoinNode) {
            JoinNode t = ((JoinNode) from);
            t.accept(this);
        }

        return null;
    }

}
