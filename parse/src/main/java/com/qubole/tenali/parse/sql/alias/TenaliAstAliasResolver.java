package com.qubole.tenali.parse.sql.alias;

import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.catalog.CatalogColumn;
import com.qubole.tenali.parse.catalog.CatalogTable;
import com.qubole.tenali.parse.sql.datamodel.*;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.SelectNode.SelectBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;


public class TenaliAstAliasResolver extends TenaliAstBaseVisitor<TenaliAstNode> {

    Map<String, String> columnAliasMap = new HashMap();

    Stack<TenaliAstNode> subQueryStack = new Stack();

    Stack<TenaliAstNode> joinSubqueryStack = new Stack();


    public TenaliAstNode visit(TenaliAstNode root) {
        System.out.println("INSIDE  TenaliAstAliasResolver ## " + root.getClass());
        if(root instanceof SelectNode) {
            return visitSelectNode(root);
        } else if(root instanceof JoinNode) {
            JoinNode join = (JoinNode) root;
            join.accept(this);
        }

        return root;
    }

    public TenaliAstNode visitSelectNode(TenaliAstNode select) {
        System.out.println("TenaliAstAliasResolver => "  + ((SelectNode) select).vid);

        SelectBuilder selectBuilder = new SelectBuilder();

        List<Triple<String, String, List<String>>> catalog = new ArrayList();

        SelectNode sNode = (SelectNode) select;

        TenaliAstNodeList with = sNode.with;
        if(with != null) {
            for(TenaliAstNode ast : with) {
                catalog.addAll(visitFromNode(ast));
            }
        }

        catalog.addAll(visitFromNode(sNode));
        System.out.println("CATALOG " + catalog);


        TenaliAstNodeList columns = sNode.columns;
        TenaliAstNodeList normalizedColumns = resolveColumns(columns, catalog);
        selectBuilder.setColumns(normalizedColumns);

        System.out.println("subQueryStack size  " + subQueryStack.size());

        if(!subQueryStack.empty()) {
            TenaliAstNode node = subQueryStack.pop();
            System.out.println("Popped => " + node.toString());
            selectBuilder.getFrom().add(node);

        } else {
            for(TenaliAstNode node : sNode.from) {
                if(node instanceof AsNode) {
                    node = ((AsNode) node).value;
                }

                selectBuilder.getFrom().add(node);
            }
        }


        if(sNode.where != null) {
            System.out.println("################### RESOLVING WHERE " + sNode.where.toString());
            TenaliAstNode where = resolveWhereCondition(sNode.where, catalog);
            System.out.println("################### RESOLVED WHERE " + where.toString());
            selectBuilder.setWhere(where);
        }

        if(sNode.groupBy != null) {
            TenaliAstNodeList groupBy = resolveColumns(sNode.groupBy, catalog);
            selectBuilder.setGroupBy(groupBy);
        }


        if(sNode.orderBy != null) {
            TenaliAstNodeList orderBy = resolveColumns(sNode.orderBy, catalog);
            selectBuilder.setOrderBy(orderBy);
        }

        System.out.println("~~~~~~~~ RETURNING  FROM  SELECT  " + sNode.vid);

        return selectBuilder.build();
    }



    // (table/subq alias, table name (null in case of subq), list of selection columns)
    public List<Triple<String, String, List<String>>> visitFromNode(TenaliAstNode root) {
        System.out.println("visitFromNode #############  " + root.getClass());
        List<Triple<String, String, List<String>>> catalog = new ArrayList<>();

        TenaliAstNode from = root;
        if(root instanceof SelectNode) {
            from = ((SelectNode) root).from;

            for(TenaliAstNode ast : (TenaliAstNodeList) from) {
                catalog.addAll(visitFromNode(ast));
            }
        }

        System.out.println("TenaliAstAliasResolver From => " + from.getClass());

        TenaliAstNode select = null;
        String alias = null;

        try {
            if(from instanceof IdentifierNode) {
                catalog = ImmutableList.of(getCatalog(alias, (IdentifierNode) from));
            } else if(from instanceof AsNode) {
                AsNode t = ((AsNode) from);
                alias = t.aliasName;
                from = t.value;

                //handle subquery with alias
                if(!(from instanceof SelectNode)) {
                    catalog = ImmutableList.of(getCatalog(alias, (IdentifierNode) from));
                }
            }


            if(from instanceof SelectNode) {
                select = visitSelectNode(from);
                catalog = ImmutableList.of(getCatalog(alias, (SelectNode) select));

                if(root instanceof SelectNode) {
                    subQueryStack.push(select);
                } else {
                    joinSubqueryStack.push(select);
                }
            } else if(from instanceof JoinNode) {
                //joinMap.put(root, from);

                JoinNode join = (JoinNode) from;
                TenaliAstNode left = join.leftNode;
                TenaliAstNode right = join.rightNode;

                List<Triple<String, String, List<String>>> catalogLeft = visitFromNode(left);
                System.out.println("###################################  1   ");
                List<Triple<String, String, List<String>>> catalogRight = visitFromNode(right);

                System.out.println("###################################  2  ");

                catalog.addAll(catalogLeft);
                catalog.addAll(catalogRight);

                JoinNode.JoinBuilder builder = new JoinNode.JoinBuilder(join);
                if(!joinSubqueryStack.empty()) {
                    builder.setRightNode(joinSubqueryStack.pop());
                }

                if(!joinSubqueryStack.empty()) {
                    builder.setLeftNode(joinSubqueryStack.pop());
                }

                System.out.println("Join  catalog ::::::: " + catalog);
                TenaliAstNode resolvedJoin = resolveJoin(builder.build(), catalog);

                subQueryStack.push(resolvedJoin);
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("Returning from visit from");
        return catalog;
    }


    private TenaliAstNode resolveJoin(TenaliAstNode from, List<Triple<String, String, List<String>>> catalog) {
        JoinNode join = (JoinNode) from;

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + ((OperatorNode) join.joinCondition).toString());
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + catalog);

        TenaliAstNode left = join.leftNode;
        TenaliAstNode right = join.rightNode;

        JoinNode.JoinBuilder builder = new JoinNode.JoinBuilder(join);

        String leftTableName = null;
        String rightTableName = null;

        if(left instanceof AsNode) {
            AsNode as = (AsNode) left;
            if(as.value instanceof IdentifierNode) {
                left = as.value;
            }
        }

        if(right instanceof AsNode) {
            AsNode as = (AsNode) right;
            if(as.value instanceof IdentifierNode) {
                right = as.value;
            }
        }

        OperatorNode condition = (OperatorNode) join.joinCondition;

        System.out.println("RESOLVING JOIN =>");

        for(Triple<String, String, List<String>> cat : catalog) {
            if (left instanceof IdentifierNode) {
                leftTableName = ((IdentifierNode) left).name;
                if (leftTableName.equals(cat.getMiddle())) {
                    builder.setLeftNode(new IdentifierNode(leftTableName));
                }
            }


            if (right instanceof IdentifierNode) {
                rightTableName = ((IdentifierNode) right).name;
                if (rightTableName.equals(cat.getMiddle())) {
                    builder.setRightNode(new IdentifierNode(rightTableName));
                }
            }
        }

        builder.setJoinCondition(resolveWhereCondition(condition, catalog));

        return builder.build();
    }


    private TenaliAstNode resolveWhereCondition(TenaliAstNode operator,
                                                    List<Triple<String, String, List<String>>> catalog) {
        OperatorNode.OperatorBuilder conditionBuilder = new OperatorNode.OperatorBuilder((OperatorNode) operator);

        System.out.println(((OperatorNode)operator).operator + "  *************resolveWhereCondition  ");

        TenaliAstNodeList normalizedOperands = new TenaliAstNodeList();

        for(TenaliAstNode node : ((OperatorNode)operator).operands) {
            if(node instanceof IdentifierNode) {
                TenaliAstNodeList columns = new TenaliAstNodeList();
                columns.add(node);
                TenaliAstNodeList resolvedColumns = resolveColumns(columns, catalog);

                if(resolvedColumns.size() > 0) {
                    normalizedOperands.add(resolvedColumns.get(0));
                }

            } else if(node instanceof LiteralNode) {
                normalizedOperands.add(new LiteralNode("TENALI_LITERAL"));
            } else if(node instanceof FunctionNode) {
                FunctionNode t = (FunctionNode) node;
                normalizedOperands.add(new IdentifierNode("TENALI_FUNCTION_" + t.functionName));
            } else {
                normalizedOperands.add(resolveWhereCondition((OperatorNode) node, catalog));
            }
        }

        conditionBuilder.setOperands(normalizedOperands);
        return conditionBuilder.build();

    }


    private TenaliAstNodeList resolveColumns(TenaliAstNodeList columns,
                                              List<Triple<String, String, List<String>>> catalog) {
        TenaliAstNodeList normalizedColumns = new TenaliAstNodeList();

        Set<String> resolvedColumns = new HashSet();
        Set<String> unResolvedColumns = new HashSet();

        for(Triple<String, String, List<String>> cat : catalog) {

            String tableAlias = cat.getLeft();
            String tableName = cat.getMiddle();
            List<String> values = cat.getRight();

            System.out.println("columnAliasMap =>  " + columnAliasMap);

            for (TenaliAstNode column : columns.getOperandlist()) {
                String name = null;
                String alias = null;
                String table = null;

                System.out.println("-----------------====================----------- " + column.toString());

                if (column instanceof AsNode) {
                    AsNode t = (AsNode) column;
                    alias = t.aliasName;
                    column = t.value;
                }

                name = getColumnName(column);
                String normalizedName = name;
                String resolvedName = null;


                if (resolvedColumns.contains(name)) {
                    continue;
                } else if (name.contains(".")) {
                    int idx = name.lastIndexOf('.');
                    table = name.substring(0, idx);
                    normalizedName = name.substring(idx + 1, name.length());
                } else if (name.startsWith("TENALI_FUNCTION_")) {
                    resolvedName = name;
                }


                if (columnAliasMap.containsKey(name)) {
                    resolvedName = columnAliasMap.get(name);
                } else if (columnAliasMap.containsKey(normalizedName)) {
                    resolvedName = columnAliasMap.get(normalizedName);
                } else if ((table != null && (table.equals(tableAlias) || table.equals(tableName)))
                        || table == null) {
                    for (String col : values) {
                        String[] tokens = col.split("\\.");
                        String colName = tokens[tokens.length - 1];

                        if (normalizedName.equals(colName)) {
                            if (col.contains(".") || tableName == null) {
                                resolvedName = col;
                            } else {
                                resolvedName = tableName + "." + col;
                            }
                        }
                    }

                }


                System.out.println(table + " :  Lookup Name : " + name + " : Normalized Name : " + normalizedName + "    ResolvedName  " + resolvedName);


                if (resolvedName != null) {
                    resolvedColumns.add(name);
                    normalizedColumns.add(new IdentifierNode(resolvedName));
                } else {
                    unResolvedColumns.add(name);
                    resolvedName = name;
                }


                if (alias != null) {
                    columnAliasMap.put(alias, resolvedName);

                    if (tableAlias != null) {
                        columnAliasMap.put(tableAlias + "." + alias, resolvedName);
                    }
                }

            }
        }

        for(String name : unResolvedColumns) {
            normalizedColumns.add(new IdentifierNode(name));
        }

        System.out.println("normalizedColumns ====  " + normalizedColumns);

        return normalizedColumns;
    }


    private String getColumnName(TenaliAstNode column) {
        String name = null;

        if(column instanceof IdentifierNode) {
            name = ((IdentifierNode) column).name;
        } else if(column instanceof FunctionNode) {
            name = "TENALI_FUNCTION_" + ((FunctionNode) column).functionName;
        } else if(column instanceof OperatorNode) {
            OperatorNode t = (OperatorNode) column;
            name =  getOperatorName(t);
        } else if(column instanceof LiteralNode) {
            name = "TENALI_LITERAL";
        }

        return name;
    }


    private String getOperatorName(OperatorNode node) {
        String name = node.operator;

        if(name.equalsIgnoreCase("COUNT")) {
            if(node.operands instanceof TenaliAstNodeList) {
                TenaliAstNode operand = node.operands.get(0);

                if(operand instanceof IdentifierNode &&
                        ((IdentifierNode) operand).name.equals("*")) {
                    name = "COUNT STAR";
                }
            }
        }

        return name;
    }


    private Pair<String, List<String>> getCatalogColumns(CatalogTable catalogTable) {
        String tableName = catalogTable.getTableName();

        List<String> columns = new ArrayList();
        for(CatalogColumn column : catalogTable.getColumns()) {
            columns.add(column.getName().toUpperCase());
        }

        return ImmutablePair.of(tableName, columns);
    }

    private List<String> getCatalogColumns(TenaliAstNodeList columns) {
        List<String> stringifiedColumns = new ArrayList();

        Iterator<TenaliAstNode> iter = columns.iterator();
        while(iter.hasNext()) {
            IdentifierNode c = (IdentifierNode) iter.next();
            stringifiedColumns.add(c.name);
        }

        return stringifiedColumns;
    }


    private Triple<String, String, List<String>> getCatalog(String alias, SelectNode select) {
        TenaliAstNodeList columns = select.columns;
        List<String> tableMeta = getCatalogColumns(columns);
        return ImmutableTriple.of(alias, null, tableMeta);
    }


    private Triple<String, String, List<String>> getCatalog(String alias, IdentifierNode node) throws Exception {
        CatalogTable catalogTable = (CatalogTable) node.accept(new CatalogResolver());
        Pair<String, List<String>> tableMeta = getCatalogColumns(catalogTable);
        return ImmutableTriple.of(alias, tableMeta.getLeft(), tableMeta.getRight());
    }

}
