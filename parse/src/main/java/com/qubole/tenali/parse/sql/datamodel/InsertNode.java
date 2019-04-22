package com.qubole.tenali.parse.sql.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qubole.tenali.parse.sql.TenaliAstBaseTransformer;


public class InsertNode extends TenaliAstNode {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final IdentifierNode table;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList staticPartitions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNodeList dynamicPartitions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final TenaliAstNode from;

    @JsonCreator
    public InsertNode(@JsonProperty("table") IdentifierNode table,
                      @JsonProperty("static_partitions") TenaliAstNodeList staticPartitions,
                      @JsonProperty("dynamic_partitions") TenaliAstNodeList dynamicPartitions,
                      @JsonProperty("from") TenaliAstNode from) {
        this.table = table;
        this.staticPartitions = staticPartitions;
        this.dynamicPartitions = dynamicPartitions;
        this.from = from;
    }


    @Override
    public Object accept(TenaliAstBaseTransformer visitor) {
        if(from != null) {
            return from.accept(visitor);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(table != null) {
            sb.append(table.toString()).append("\n");
        }

        if(staticPartitions != null) {
            sb.append(staticPartitions.toString()).append("\n");
        }

        if(dynamicPartitions != null) {
            sb.append(dynamicPartitions.toString()).append("\n");
        }

        if(from != null) {
            sb.append(from.toString()).append("\n");
        }

        return sb.toString();
    }

    public static class InsertBuilder implements Builder<TenaliAstNode> {
        IdentifierNode table;
        TenaliAstNodeList staticPartitions;
        TenaliAstNodeList dynamicPartitions;
        TenaliAstNode from;

        public InsertBuilder(InsertNode node) {
            assert node != null;

            this.table = node.table;
            this.staticPartitions = node.staticPartitions;
            this.dynamicPartitions = node.dynamicPartitions;
            this.from = node.from;
        }


        public TenaliAstNode build() {
            return new InsertNode(table,
                    staticPartitions,
                    dynamicPartitions,
                    from);
        }

        public void setFrom(TenaliAstNode from) {
            this.from = from;
        }

    }
}
