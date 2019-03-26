package com.qubole.tenali.parse.sql.visitor;

import com.google.common.collect.ImmutableList;
import com.qubole.tenali.parse.catalog.CatalogColumn;
import com.qubole.tenali.parse.catalog.CatalogTable;
import com.qubole.tenali.parse.catalog.CatalogResolver;
import com.qubole.tenali.parse.sql.datamodel.*;
import com.qubole.tenali.parse.sql.TenaliAstBaseVisitor;
import com.qubole.tenali.parse.sql.datamodel.SelectNode.SelectBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;


public class TenaliAstAliasResolver extends TenaliAstBaseVisitor<TenaliAstNode> {

    List<Stack<TenaliAstNode>> subQueryStack = new ArrayList();

    Stack<List<Triple<String, String, List<String>>>> catalogStack = new Stack();

    Map<String, Object> columnAliasMap = new HashMap();

    CatalogResolver catalogResolver;

    public TenaliAstAliasResolver(CatalogResolver catalogResolver) {
        super();
        this.catalogResolver = catalogResolver;
    }


    public TenaliAstNode visit(TenaliAstNode root) {
        try {
            if(root instanceof DDLNode) {
                DDLNode ddl = (DDLNode) root;
                String ddlToken = ddl.ddlToken;
                TenaliAstNode tableNode = ddl.tableNode;

                if(tableNode != null && ddl.selectNode != null) {
                    if(ddl.selectNode instanceof SelectNode) {
                        SelectNode select = (SelectNode) visitSelectNode(0, ddl.selectNode);

                        TenaliAstNodeList keywords = select.keywords;
                        if(keywords == null) {
                            keywords = new TenaliAstNodeList();
                        }
                        keywords.add(new OperatorNode("CTAS",
                                new TenaliAstNodeList().add(tableNode)));

                        SelectBuilder builder = new SelectBuilder(select);
                        builder.setKeywords(keywords);
                        return builder.build();
                    } else if(ddl.selectNode instanceof OperatorNode) {
                        return root;
                    }
                }

            } else if (root instanceof SelectNode) {
                return visitSelectNode(0, root);
            } else if (root instanceof JoinNode) {
                JoinNode join = (JoinNode) root;
                join.accept(this);
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return root;
    }


    public TenaliAstNode visitSelectNode(int scope, TenaliAstNode select) throws Exception {
        System.out.println("TenaliAstAliasResolver => ");

        SelectBuilder selectBuilder = new SelectBuilder();

        List<Triple<String, String, List<String>>> catalog = new ArrayList();
        while (!catalogStack.isEmpty()) {
            catalog.addAll(catalogStack.pop());
        }


        SelectNode sNode = (SelectNode) select;

        // WITH
        TenaliAstNodeList with = sNode.with;
        if (with != null) {
            for (TenaliAstNode ast : with) {
                catalog.addAll(visitFromNode(scope, null, ast));
            }
        }

        // FROM
        for (TenaliAstNode ast : sNode.from) {
            catalog.addAll(visitFromNode(scope, null, ast));
            System.out.println("SELECT  CATALOG " + catalog);
        }

        while (!subQueryStack.get(scope).empty()) {
            TenaliAstNode node = subQueryStack.get(scope).pop();
            selectBuilder.getFrom().add(node);
        }


        catalogStack.push(catalog);

        // Columns
        TenaliAstNodeList columns = sNode.columns;
        TenaliAstNodeList normalizedColumns = resolveColumns(columns, catalog, columnAliasMap);
        selectBuilder.setColumns(normalizedColumns);


        // Where
        if (sNode.where != null) {
            TenaliAstNode where = resolveWhereCondition(sNode.where, catalog);
            selectBuilder.setWhere(where);
        }

        // Group By
        if (sNode.groupBy != null) {
            TenaliAstNodeList groupBy = resolveColumns(sNode.groupBy, catalog, columnAliasMap);
            selectBuilder.setGroupBy(groupBy);
        }


        // Order By
        if (sNode.orderBy != null) {
            TenaliAstNodeList orderBy = resolveColumns(sNode.orderBy, catalog, columnAliasMap);
            selectBuilder.setOrderBy(orderBy);
        }

        // Keywords
        if(sNode.keywords != null && sNode.keywords.size() > 0) {
            selectBuilder.setKeywords(sNode.keywords);
        }


        System.out.println("~~~~~~~~ RETURNING  FROM  SELECT --- " );

        TenaliAstNode resolvedSelect = selectBuilder.build();
        return resolvedSelect;
    }


    // (table/subq alias, table name (null in case of subq), list of selection columns)
    public List<Triple<String, String, List<String>>> visitFromNode(
            int scope, String alias, TenaliAstNode from) throws Exception {
        System.out.println("visitFromNode #############  " + from.getClass());
        List<Triple<String, String, List<String>>> catalog = new ArrayList<>();

        TenaliAstNode select = null;

        if (from instanceof IdentifierNode) {
            catalog = ImmutableList.of(getCatalog(alias, ((IdentifierNode) from).name, from));
            System.out.println("########PUSING STACK TABLE ###########################   ");
            push(scope, from);
        } else if (from instanceof AsNode) {
            AsNode t = ((AsNode) from);

            catalog = visitFromNode(scope, t.aliasName, t.value);
        } else if (from instanceof SelectNode) {
            select = visitSelectNode(scope + 1, from);
            catalog = ImmutableList.of(getCatalog(alias, null, select));
            System.out.println("########PUSING STACK SELECT ###########################   ");
            push(scope, select);
        } else if (from instanceof JoinNode) {
            //joinMap.put(root, from);

            JoinNode join = (JoinNode) from;
            TenaliAstNode left = join.leftNode;
            TenaliAstNode right = join.rightNode;
            TenaliAstNode condition = join.joinCondition;
            String joinType = join.joinType;

            List<Triple<String, String, List<String>>> catalogLeft = visitFromNode(scope, null, left);
            System.out.println(" ############### ########## ##########  1   ");
            List<Triple<String, String, List<String>>> catalogRight = visitFromNode(scope, null, right);
            System.out.println(" ############### ########## ##########  2   ");

            catalog.addAll(catalogLeft);
            catalog.addAll(catalogRight);

            JoinNode.JoinBuilder builder = new JoinNode.JoinBuilder();
            if (!subQueryStack.get(scope).empty()) {
                builder.setRightNode(subQueryStack.get(scope).pop());
            }

            if (!subQueryStack.get(scope).empty()) {
                builder.setLeftNode(subQueryStack.get(scope).pop());
            }

            System.out.println("Join  catalog ::::::: " + catalog);

            builder.setJoinType(joinType);
            builder.setJoinCondition(resolveWhereCondition(condition, catalog));

            //TenaliAstNode resolvedJoin = resolveJoin(builder.build(), catalog);
            System.out.println("######## PUSING STACK JOIN ###########################   ");
            //push(scope, resolvedJoin);
            push(scope, builder.build());
        } else if (from instanceof FunctionNode) {
            FunctionNode func = (FunctionNode) from;
            TenaliAstNode ast = (TenaliAstNode) from.accept(new FunctionResolver(catalog, columnAliasMap));
            catalog = ImmutableList.of(getCatalog(alias, func.functionName, func.arguments));
            System.out.println("########PUSING STACK FUNCTION ###########################   " + ast.toString());
            push(scope, ast);
        }

        System.out.println("FROM  CATALOG " + catalog);
        return catalog;
    }


    private TenaliAstNode resolveWhereCondition(TenaliAstNode operator,
                                                List<Triple<String, String, List<String>>> catalog) {
        return (TenaliAstNode) operator.accept(new OperatorResolver(catalog, columnAliasMap));
    }


    private Pair<String, List<String>> getCatalogColumns(CatalogTable catalogTable) {
        String tableName = catalogTable.getTableName().toUpperCase();

        List<String> columns = new ArrayList();
        for (CatalogColumn column : catalogTable.getColumns()) {
            columns.add(column.getName().toUpperCase());
        }

        return ImmutablePair.of(tableName, columns);
    }

    private List<String> getCatalogColumns(TenaliAstNodeList columns) {
        List<String> stringifiedColumns = new ArrayList();

        Iterator<TenaliAstNode> iter = columns.iterator();
        while (iter.hasNext()) {
            TenaliAstNode node = iter.next();
            if(node instanceof IdentifierNode) {
                stringifiedColumns.add(((IdentifierNode) node).name);
            } else if(node instanceof IdentifierNode) {
                stringifiedColumns.add(((FunctionNode) node).functionName);
            } else {
                stringifiedColumns.add(node.toString());
            }
        }

        return stringifiedColumns;
    }


    private Triple<String, String, List<String>> getCatalog(String alias, String tableName, TenaliAstNode node) throws Exception {
        Triple<String, String, List<String>> retVal;
        if (node instanceof TenaliAstNodeList) {
            List<String> tableMeta = getCatalogColumns((TenaliAstNodeList) node);
            retVal = ImmutableTriple.of(alias, tableName, tableMeta);
        } else if (node instanceof SelectNode) {
            TenaliAstNodeList columns = ((SelectNode) node).columns;
            List<String> tableMeta = getCatalogColumns(columns);
            retVal = ImmutableTriple.of(alias, null, tableMeta);
        } else {
            CatalogTable catalogTable = (CatalogTable) node.accept(catalogResolver);
            Pair<String, List<String>> tableMeta = getCatalogColumns(catalogTable);
            retVal = ImmutableTriple.of(alias, tableMeta.getLeft(), tableMeta.getRight());
        }

        return retVal;
    }


    private void push(int scope, TenaliAstNode ast) {
        int size = subQueryStack.size();
        for(int i = size; i <= scope; i++) {
            Stack<TenaliAstNode> stack = new Stack();
            subQueryStack.add(stack);
        }

        if (ast instanceof AsNode) {
            ast = ((AsNode) ast).value;
        }

        subQueryStack.get(scope).push(ast);
    }

}
