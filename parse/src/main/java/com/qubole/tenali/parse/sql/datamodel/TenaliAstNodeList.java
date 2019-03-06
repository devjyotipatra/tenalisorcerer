package com.qubole.tenali.parse.sql.datamodel;


import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TenaliAstNodeList extends TenaliAstNode implements Iterable<TenaliAstNode> {
    private final List<TenaliAstNode> list = new ArrayList<>();

    public TenaliAstNodeList() { }

    public TenaliAstNodeList(List<TenaliAstNode> nodeList) {
        assert nodeList != null;
        this.list.addAll(nodeList);
    }

    public TenaliAstNodeList(TenaliAstNodeList nodeList) {
        assert nodeList != null;
        add(nodeList);
    }

    public TenaliAstNodeList add(TenaliAstNode node) {
        assert list != null;
        if(node instanceof TenaliAstNodeList) {
            list.addAll(((TenaliAstNodeList) node).getOperandlist());
        } else {
            list.add(node);
        }

        return this;
    }

    public void add(TenaliAstNodeList nodeList) {
        for(TenaliAstNode node : nodeList) {
            add(node);
        }
    }

    public void add(List<TenaliAstNode> nodeList) {
        for(TenaliAstNode node : nodeList) {
            add(node);
        }
    }

    public void addAll(TenaliAstNodeList nodeList) {
        add(nodeList.getOperandlist());
    }

    public Iterator<TenaliAstNode> iterator() {
        return list.iterator();
    }

    public List<TenaliAstNode> getOperandlist() {
        return list;
    }


    public TenaliAstNode get(int i) {return list.get(i);}

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(this);
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
                TenaliAstNode node = (TenaliAstNode) iter.next();
                sb.append(node.toString()).append(",");
            }
        }

        return sb.toString();
    }

    public static class NodeListBuilder implements Builder<TenaliAstNodeList> {
        List<TenaliAstNode> list = new ArrayList<>();

        public TenaliAstNodeList build() {
            TenaliAstNodeList nodeList = new TenaliAstNodeList();
            nodeList.add(list);

            return nodeList;
        }

        public void addNode(TenaliAstNode node) {
            list.add(node);
        }

        public List<TenaliAstNode> getList() {
            return list;
        }

        public void setList(List<TenaliAstNode> list) {
            this.list = list;
        }
    }
}
