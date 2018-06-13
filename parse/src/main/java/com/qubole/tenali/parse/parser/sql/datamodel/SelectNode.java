package com.qubole.tenali.parse.parser.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SelectNode extends BaseASTNode {
    public final BaseASTNode where;
    public final List<BaseASTNode> orderBy;
    public final List<BaseASTNode> groupBy;
    public final List<String> project;
    public final List<BaseASTNode> from;
    public final List<BaseASTNode> with;
    public final List<BaseASTNode> columns;
    public final List<BaseASTNode> keywords;
    public final BaseASTNode having;

    @JsonCreator
    SelectNode(@JsonProperty("where") BaseASTNode where,
               @JsonProperty("orderBy") List<BaseASTNode> orderBy,
               @JsonProperty("groupBy") List<BaseASTNode> groupBy,
               @JsonProperty("project") List<String> project,
               @JsonProperty("from") List<BaseASTNode> from,
               @JsonProperty("with") List<BaseASTNode> with,
               @JsonProperty("columns") List<BaseASTNode> columns,
               @JsonProperty("keywords") List<BaseASTNode> keywords,
               @JsonProperty("having") BaseASTNode having) {
        this.where = where;
        this.orderBy = orderBy;
        this.groupBy = groupBy;
        this.from = from;
        this.project = project;
        this.with = with;
        this.columns = columns;
        this.keywords = keywords;
        this.having = having;
    }
}

