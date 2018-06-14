package com.qubole.tenali.parse.parser.sql.datamodel;


import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseAstNodeList extends BaseAstNode implements Iterable<BaseAstNode> {
    private final List<BaseAstNode> list;

    public static final BaseAstNodeList EMPTY =
            new BaseAstNodeList() {
                public void addNode(BaseAstNode node) {
                    add(node);
                }
            };

    public BaseAstNodeList() {
        list = new ArrayList<>();
    }

    public void add(BaseAstNode node) {
        list.add(node);
    }

    public Iterator<BaseAstNode> iterator() {
        return list.iterator();
    }

    @Override
    public List<BaseAstNode> getOperandlist() {
        return list;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        for(BaseAstNode node : list) {
            node.accept(visitor);
        }
    }
}
