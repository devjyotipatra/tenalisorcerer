package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.TenaliAstNodeVisitor;

import java.security.SecureRandom;
import java.util.Random;


public class SelectNode extends TenaliAstNode {
    public final TenaliAstNode where;
    public final TenaliAstNode orderBy;
    public final TenaliAstNodeList groupBy;
    public final TenaliAstNode from;
    public final TenaliAstNodeList with;
    public final TenaliAstNodeList columns;
    public final TenaliAstNodeList keywords;
    public final TenaliAstNode having;
    public final TenaliAstNodeList windowDecls;

    public final int vid;

    final RandomInt random = new RandomInt(8);

    @JsonCreator
    SelectNode(@JsonProperty("where") TenaliAstNode where,
               @JsonProperty("orderBy") TenaliAstNode orderBy,
               @JsonProperty("groupBy") TenaliAstNodeList groupBy,
               @JsonProperty("from") TenaliAstNode from,
               @JsonProperty("with") TenaliAstNodeList with,
               @JsonProperty("columns") TenaliAstNodeList columns,
               @JsonProperty("keywords") TenaliAstNodeList keywords,
               @JsonProperty("having") TenaliAstNode having,
               @JsonProperty("window") TenaliAstNodeList windowDecls) {
        this.where = where;
        this.orderBy = orderBy;
        this.groupBy = groupBy;
        this.from = from;
        this.with = with;
        this.columns = columns;
        this.keywords = keywords;
        this.having = having;
        this.windowDecls = windowDecls;

        vid = random.nextInt();
    }


    /*public List<BaseAstNode> getOperandlist() {
        return ImmutableNullableList.of(where, orderBy, groupBy, from,
                with, columns, keywords, having, windowDecls);
    }*/

    @Override
    public void accept(TenaliAstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public boolean hasOrderBy() {
        return orderBy != null;
    }

    public boolean hasGroupBy() {
        return groupBy != null && groupBy.getOperandlist().size() != 0;
    }

    public boolean hasWhere() {
        return where != null;
    }

    public boolean hasWindow() {
        return windowDecls != null && windowDecls.getOperandlist().size() != 0;
    }

    public boolean hasWith() {
        return with != null && with.getOperandlist().size() != 0;
    }

    public boolean hasHaving() {
        return having != null;
    }


    @Override
    public boolean equals(Object obj) {
        return vid == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return vid;
    }

    @Override
    public String toString() {
        StringBuilder sb  = new StringBuilder();

        if(where != null) {
            sb.append("where: ").append(where.toString()).append("\n");
        }

        if(orderBy != null) {
            sb.append("orderBy: ").append(orderBy.toString()).append("\n");
        }

        if(groupBy != null) {
            sb.append("groupBy: ").append(groupBy.toString()).append("\n");
        }

        if(from != null) {
            sb.append("from: ").append(from.toString()).append("\n");
        }

        if(with != null) {
            sb.append("with: ").append(with.toString()).append("\n");
        }

        if(columns != null) {
            sb.append("columns: ").append(columns.toString()).append("\n");
        }

        if(keywords != null) {
            sb.append("keywords: ").append(keywords.toString()).append("\n");
        }

        if(having != null) {
            sb.append("having: ").append(having.toString()).append("\n");
        }

        if(windowDecls != null) {
            sb.append("windowDecls: ").append(windowDecls.toString());
        }

        return sb.toString();
    }


    public static class SelectBuilder implements Builder<TenaliAstNode> {
        TenaliAstNode where;
        TenaliAstNode orderBy;
        TenaliAstNodeList groupBy;
        TenaliAstNode from;
        TenaliAstNodeList with;
        TenaliAstNodeList columns;
        TenaliAstNodeList keywords;
        TenaliAstNode having;
        TenaliAstNodeList windowDecls;

        public SelectBuilder() {}

        public SelectBuilder(SelectNode select) {
            assert select != null;

            setWhere(select.where);
            setOrderBy(select.orderBy);
            setGroupBy(select.groupBy);
            setFrom(select.from);
            setWith(select.with);
            setColumns(select.columns);
            setKeywords(select.keywords);
            setHaving(select.having);
            setWindowDecls(select.windowDecls);
        }

        @Override
        public TenaliAstNode build() {
            return new SelectNode(where,
                    orderBy,
                    groupBy,
                    from,
                    with,
                    columns,
                    keywords,
                    having,
                    windowDecls);
        }

        public TenaliAstNode getWhere() {
            return where;
        }

        public void setWhere(TenaliAstNode where) {
            this.where = where;
        }

        public TenaliAstNode getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(TenaliAstNode orderBy) {
            this.orderBy = orderBy;
        }

        public TenaliAstNodeList getGroupBy() {
            return groupBy;
        }

        public void setGroupBy(TenaliAstNodeList groupBy) {
            this.groupBy = groupBy;
        }

        public TenaliAstNode getFrom() {
            return from;
        }

        public void setFrom(TenaliAstNode from) {
            this.from = from;
        }

        public TenaliAstNodeList getWith() {
            return with;
        }

        public void setWith(TenaliAstNodeList with) {
            this.with = with;
        }

        public TenaliAstNodeList getColumns() {
            return columns;
        }

        public void setColumns(TenaliAstNodeList columns) {
            this.columns = columns;
        }

        public TenaliAstNodeList getKeywords() {
            return keywords;
        }

        public void setKeywords(TenaliAstNodeList keywords) {
            this.keywords = keywords;
        }

        public TenaliAstNode getHaving() {
            return having;
        }

        public void setHaving(TenaliAstNode having) {
            this.having = having;
        }

        public TenaliAstNodeList getWindowDecls() {
            return windowDecls;
        }

        public void setWindowDecls(TenaliAstNodeList windowDecls) {
            this.windowDecls = windowDecls;
        }
    }

    private class RandomInt {
        final String digits = "1234567890987654321";

        Random random = new SecureRandom();

        final char[] symbols;

        final char[] buf;

        public RandomInt(int length) {
            this.symbols = digits.toCharArray();
            this.buf = new char[length];
        }

        public int nextInt() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return Integer.parseInt(new String(buf));
        }
    }
}

