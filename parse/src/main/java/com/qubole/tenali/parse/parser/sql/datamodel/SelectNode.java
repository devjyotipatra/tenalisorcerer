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
}

