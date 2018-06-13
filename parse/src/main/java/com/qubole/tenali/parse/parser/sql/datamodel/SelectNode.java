package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.parser.sql.visitor.BaseASTNodeVisitor;
import org.apache.calcite.util.ImmutableNullableList;

import java.util.List;

public class SelectNode extends BaseASTNode {
    public final BaseASTNode where;
    public final BaseASTNodeList orderBy;
    public final BaseASTNodeList groupBy;
    public final BaseASTNode from;
    public final BaseASTNodeList with;
    public final BaseASTNodeList columns;
    public final BaseASTNodeList keywords;
    public final BaseASTNode having;
    public final BaseASTNodeList windowDecls;

    @JsonCreator
    SelectNode(@JsonProperty("where") BaseASTNode where,
               @JsonProperty("orderBy") BaseASTNodeList orderBy,
               @JsonProperty("groupBy") BaseASTNodeList groupBy,
               @JsonProperty("from") BaseASTNode from,
               @JsonProperty("with") BaseASTNodeList with,
               @JsonProperty("columns") BaseASTNodeList columns,
               @JsonProperty("keywords") BaseASTNodeList keywords,
               @JsonProperty("having") BaseASTNode having,
               @JsonProperty("window") BaseASTNodeList windowDecls) {
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
    public List<BaseASTNode> getOperandlist() {
        return ImmutableNullableList.of(where, orderBy, groupBy, from,
                with, columns, keywords, having, windowDecls);
    }

    @Override
    public void accept(BaseASTNodeVisitor visitor) {
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

