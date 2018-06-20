package com.qubole.tenali.parse.parser.sql.datamodel;


import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BaseAstNodeList extends BaseAstNode implements Iterable<BaseAstNode> {
    private final List<BaseAstNode> list = new ArrayList<>();

    public BaseAstNodeList() { }

    public BaseAstNodeList(List<BaseAstNode> nodeList) {
        assert nodeList != null;
        this.list.addAll(nodeList);
    }

    public BaseAstNodeList(BaseAstNodeList nodeList) {
        assert nodeList != null;
        add(nodeList);
    }

    public void add(BaseAstNode node) {
        list.add(node);
    }

    public void add(BaseAstNodeList nodeList) {
        assert list != null;
        for(BaseAstNode node : nodeList) {
            list.add(node);
        }
    }

    public void add(List<BaseAstNode> nodeList) {
        for(BaseAstNode node : nodeList) {
            list.add(node);
        }
    }

    public Iterator<BaseAstNode> iterator() {
        return list.iterator();
    }

    public List<BaseAstNode> getOperandlist() {
        return list;
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        for(BaseAstNode node : list) {
            node.accept(visitor);
        }
    }

    public int size() {
        return list == null ? 0 : list.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(list != null) {
            Iterator iter = iterator();
            while(iter.hasNext()) {
                BaseAstNode node = (BaseAstNode) iter.next();
                sb.append(node.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    public static class NodeListBuilder implements Builder {
        List<BaseAstNode> list = new ArrayList<>();

        public BaseAstNodeList build() {
            BaseAstNodeList nodeList = new BaseAstNodeList();
            nodeList.add(list);

            return nodeList;
        }

        public List<BaseAstNode> getList() {
            return list;
        }

        public void setList(List<BaseAstNode> list) {
            this.list = list;
        }
    }
}
