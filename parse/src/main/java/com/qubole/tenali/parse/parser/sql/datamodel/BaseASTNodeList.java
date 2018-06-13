package com.qubole.tenali.parse.parser.sql.datamodel;


import com.qubole.tenali.parse.parser.sql.visitor.BaseASTNodeVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseASTNodeList extends BaseASTNode implements Iterable<BaseASTNode> {
    private final List<BaseASTNode> list;

    public static final BaseASTNodeList EMPTY =
            new BaseASTNodeList() {
                public void addNode(BaseASTNode node) {
                    add(node);
                }
            };

    public BaseASTNodeList() {
        list = new ArrayList<>();
    }

    public void add(BaseASTNode node) {
        list.add(node);
    }

    public Iterator<BaseASTNode> iterator() {
        return list.iterator();
    }

    @Override
    public List<BaseASTNode> getOperandlist() {
        return list;
    }

    @Override
    public void accept(BaseASTNodeVisitor visitor) {
        for(BaseASTNode node : list) {
            node.accept(visitor);
        }
    }
}
