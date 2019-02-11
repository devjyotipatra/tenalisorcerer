package com.qubole.tenali.parse.util;

import com.qubole.tenali.parse.sql.datamodel.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class VisitorFunctions {

    private List<Pair<String, String>> findTables(TenaliAstNode root) {
        List<Pair<String, String>> tables = new ArrayList<>();

        if(root instanceof JoinNode) {
            TenaliAstNode leftChild = ((JoinNode) root).leftNode;
            TenaliAstNode rightChild = ((JoinNode) root).rightNode;

            if(!(isJoinTable(leftChild) && isJoinTable(rightChild))) {
                return tables;
            }

            if(leftChild instanceof IdentifierNode) {
                tables.add(new ImmutablePair(null, ((IdentifierNode) leftChild).name));
            } else if (leftChild instanceof AsNode) {
                tables.add(new ImmutablePair(((AsNode) leftChild).aliasName,
                        ((IdentifierNode) ((AsNode) leftChild).value).name));
            }

            if(rightChild instanceof IdentifierNode) {
                tables.add(new ImmutablePair(null, ((IdentifierNode) rightChild).name));
                tables.add(new ImmutablePair(((AsNode) rightChild).aliasName,
                        ((IdentifierNode) ((AsNode) rightChild).value).name));
            }
        }
        else if (root instanceof AsNode) {
            TenaliAstNode valNode = ((AsNode) root).value;

            if (valNode instanceof IdentifierNode) {
                tables.add(new ImmutablePair(((AsNode) root).aliasName, ((IdentifierNode) valNode).name));
            }
        }
        else if (root instanceof IdentifierNode) {
            tables.add(new ImmutablePair(null, ((IdentifierNode) root).name));
        }
        return tables;
    }


    public boolean isJoinTable(TenaliAstNode child) {
        if (child == null ) {
            return false;
        }

        if(child instanceof AsNode) {
            TenaliAstNode node = ((AsNode) child).value;
            if(node instanceof SelectNode) {
                return false;
            }
        }
        else if (child instanceof SelectNode) {
            return false;
        }

        return true;
    }
}
