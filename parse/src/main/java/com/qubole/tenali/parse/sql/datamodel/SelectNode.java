package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.util.exception.NotImplementedException;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;


public class SelectNode extends TenaliAstNode {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNode where;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList orderBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList groupBy;

    public final TenaliAstNodeList from;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList with;

    public final TenaliAstNodeList columns;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList keywords;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNode having;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList windowDecls;

    //public static int vid = 0;

    @JsonCreator
    SelectNode(@JsonProperty("where") TenaliAstNode where,
               @JsonProperty("orderBy") TenaliAstNodeList orderBy,
               @JsonProperty("groupBy") TenaliAstNodeList groupBy,
               @JsonProperty("from") TenaliAstNodeList from,
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

        //vid = vid + 1;
    }


    /*public List<BaseAstNode> getOperandlist() {
        return ImmutableNullableList.of(where, orderBy, groupBy, from,
                with, columns, keywords, having, windowDecls);
    }*/

    @Override
    public Object accept(TenaliAstBaseVisitor visitor) {
        return visitor.visit(this);
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


    //public int getVid() {return vid;}


    /*@Override
    public boolean equals(Object obj) {
        return vid == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return vid;
    }*/

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
        TenaliAstNodeList orderBy;
        TenaliAstNodeList groupBy;
        TenaliAstNodeList from;
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

        public TenaliAstNodeList getOrderBy() {
            if(orderBy == null) {
                orderBy = new TenaliAstNodeList();
            }
            return orderBy;
        }

        public void setOrderBy(TenaliAstNodeList orderBy) {
            this.orderBy = orderBy;
        }

        public TenaliAstNodeList getGroupBy() {
            return groupBy;
        }

        public void setGroupBy(TenaliAstNodeList groupBy) {
            this.groupBy = groupBy;
        }

        public TenaliAstNodeList getFrom() {
            if(from == null) {
                from = new TenaliAstNodeList();
            }

            return from;
        }

        public void setFrom(TenaliAstNodeList from) {
            this.from = from;
        }

        public TenaliAstNodeList getWith() {
            if(with == null) {
                with = new TenaliAstNodeList();
            }
            return with;
        }

        public void setWith(TenaliAstNodeList with) {
            this.with = with;
        }

        public TenaliAstNodeList getColumns() {
            if(columns == null) {
                columns = new TenaliAstNodeList();
            }
            return columns;
        }

        public void setColumns(TenaliAstNodeList columns) {
            this.columns = columns;
        }

        public void setColumns(List<String> columns) {
            TenaliAstNodeList list = new TenaliAstNodeList();
            for(String column : columns) {
                list.add(new IdentifierNode(column));
            }
            this.columns = list;
        }

        public TenaliAstNodeList getKeywords() {
            if(keywords == null) {
                keywords = new TenaliAstNodeList();
            }
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
}

