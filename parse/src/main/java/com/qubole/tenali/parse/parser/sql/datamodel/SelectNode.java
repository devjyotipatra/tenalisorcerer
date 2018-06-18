package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseAstNodeVisitor;
import org.apache.calcite.util.ImmutableNullableList;

import java.util.List;

public class SelectNode extends BaseAstNode {
    public final BaseAstNode where;
    public final BaseAstNodeList orderBy;
    public final BaseAstNodeList groupBy;
    public final BaseAstNode from;
    public final BaseAstNodeList with;
    public final BaseAstNodeList columns;
    public final BaseAstNodeList keywords;
    public final BaseAstNode having;
    public final BaseAstNodeList windowDecls;

    @JsonCreator
    SelectNode(@JsonProperty("where") BaseAstNode where,
               @JsonProperty("orderBy") BaseAstNodeList orderBy,
               @JsonProperty("groupBy") BaseAstNodeList groupBy,
               @JsonProperty("from") BaseAstNode from,
               @JsonProperty("with") BaseAstNodeList with,
               @JsonProperty("columns") BaseAstNodeList columns,
               @JsonProperty("keywords") BaseAstNodeList keywords,
               @JsonProperty("having") BaseAstNode having,
               @JsonProperty("window") BaseAstNodeList windowDecls) {
        this.where = where;
        this.orderBy = orderBy;
        this.groupBy = groupBy;
        this.from = from;
        this.with = with;
        this.columns = columns;
        this.keywords = keywords;
        this.having = having;
        this.windowDecls = windowDecls;
    }

    @Override
    public List<BaseAstNode> getOperandlist() {
        return ImmutableNullableList.of(where, orderBy, groupBy, from,
                with, columns, keywords, having, windowDecls);
    }

    @Override
    public void accept(BaseAstNodeVisitor visitor) {
        visitor.visit(this);
    }

    public boolean hasOrderBy() {
        return orderBy != null && orderBy.getOperandlist().size() != 0;
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


    public static class SelectBuilder implements Builder {
        BaseAstNode where;
        BaseAstNodeList orderBy;
        BaseAstNodeList groupBy;
        BaseAstNode from;
        BaseAstNodeList with;
        BaseAstNodeList columns;
        BaseAstNodeList keywords;
        BaseAstNode having;
        BaseAstNodeList windowDecls;
        @Override
        public BaseAstNode build() {
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

        public BaseAstNode getWhere() {
            return where;
        }

        public void setWhere(BaseAstNode where) {
            this.where = where;
        }

        public BaseAstNodeList getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(BaseAstNodeList orderBy) {
            this.orderBy = orderBy;
        }

        public BaseAstNodeList getGroupBy() {
            return groupBy;
        }

        public void setGroupBy(BaseAstNodeList groupBy) {
            this.groupBy = groupBy;
        }

        public BaseAstNode getFrom() {
            return from;
        }

        public void setFrom(BaseAstNode from) {
            this.from = from;
        }

        public BaseAstNodeList getWith() {
            return with;
        }

        public void setWith(BaseAstNodeList with) {
            this.with = with;
        }

        public BaseAstNodeList getColumns() {
            return columns;
        }

        public void setColumns(BaseAstNodeList columns) {
            this.columns = columns;
        }

        public BaseAstNodeList getKeywords() {
            return keywords;
        }

        public void setKeywords(BaseAstNodeList keywords) {
            this.keywords = keywords;
        }

        public BaseAstNode getHaving() {
            return having;
        }

        public void setHaving(BaseAstNode having) {
            this.having = having;
        }

        public BaseAstNodeList getWindowDecls() {
            return windowDecls;
        }

        public void setWindowDecls(BaseAstNodeList windowDecls) {
            this.windowDecls = windowDecls;
        }
    }
}

