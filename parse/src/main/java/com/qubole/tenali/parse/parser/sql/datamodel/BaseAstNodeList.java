package com.qubole.tenali.parse.parser.sql.datamodel;


import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseAstNodeList extends BaseAstNode implements Iterable<BaseAstNode> {
    private final List<BaseAstNode> list;

    //public final BaseAstNodeList EMPTY = new BaseAstNodeList();

    public BaseAstNodeList() {
        list = new ArrayList<>();
    }

    public void add(BaseAstNode node) {
        list.add(node);
    }

    public void add(List<BaseAstNode> nodeList) {
        for(BaseAstNode node : nodeList) {
            list.add(node);
        }
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

    public int size() {
        return list == null ? 0 : list.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        System.out.println("LIST LENGTH ==> " + list.size());

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
