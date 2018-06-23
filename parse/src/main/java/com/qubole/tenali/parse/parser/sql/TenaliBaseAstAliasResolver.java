package com.qubole.tenali.parse.parser.sql;

import com.qubole.tenali.metastore.CachingMetastoreClient;
import com.qubole.tenali.parse.parser.sql.datamodel.*;
import jdk.nashorn.internal.ir.BaseNode;
import org.apache.calcite.util.Pair;
import scala.collection.mutable.ArrayStack;

import java.util.*;


public class TenaliBaseAstAliasResolver {

    Map<String, String> columnAlias = new HashMap<>();
    Map<String, String> tableAlias = new HashMap<>();
    Map<String, String> subqueryAlias = new HashMap<>();

    CachingMetastoreClient client = null;

    public TenaliBaseAstAliasResolver() {
        //cache ttl: 1 week.
        int TTL_MINS = 10080;

        //missingCache ttl: 1 day
        int MISSINGTTL_MINS = 1440;

        /*IMetaStoreClient apimetastoreClient =
                new APIMetastoreClient(accountId, apiUrl, apiToken);
        metastoreClient = new CachingMetastoreClient(
                redisEndpoint, String.valueOf(accountId), TTL_MINS, apimetastoreClient,
                MISSINGTTL_MINS, true);*/
    }


    public void dfsFindAlias(BaseAstNode root) {

        Set<BaseAstNode> visitedSubQueries = new HashSet<>();

        if (root instanceof SelectNode) {
            dfs(root, visitedSubQueries);
        }
    }



    private void dfsSubQuery(BaseAstNode root, Set<BaseAstNode> visitedSubQueries) {
        Stack<BaseAstNode> selectNodeStack = new Stack<>();

        Stack<String> aliasStack = new Stack<>();

        Map<String, String> resolvedSubQColumns = new HashMap<>();

        //BaseAstNode from = ((SelectNode) root).from;
        selectNodeStack.push(root);
        aliasStack.push("#NONE");


        while (!selectNodeStack.isEmpty()) {
            BaseAstNode node = selectNodeStack.peek();

            BaseAstNode from = ((SelectNode) node).from;

            List<String> tables = getTables(from);

            if (tables.size() == 0) {
                if (from instanceof AsNode) {
                    AsNode asNode = (AsNode) from;
                    String alias = asNode.aliasName;
                    BaseAstNode valNode = asNode.value;

                    if (valNode instanceof SelectNode) {
                        selectNodeStack.push(valNode);
                        aliasStack.push(alias);
                    } else {
                        ////
                    }
                } else if (from instanceof JoinNode) {

                } else if (from instanceof SelectNode) {
                    selectNodeStack.push(from);
                    aliasStack.push("#NONE");
                } else {
                    ////
                }
            }
        }
    }


    private List<String> getTables(BaseAstNode root) {
        List<String> tables = new ArrayList<>();

        if(root instanceof JoinNode) {
            BaseAstNode leftChild = ((JoinNode) root).leftNode;
            BaseAstNode rightChild = ((JoinNode) root).rightNode;

            if(leftChild instanceof IdentifierNode) {
                tables.add(((IdentifierNode) leftChild).name);
            } else if (leftChild instanceof AsNode) {
                tables.add(((IdentifierNode) ((AsNode) leftChild).value).name);
            }

            if(rightChild instanceof IdentifierNode) {
                tables.add(((IdentifierNode) rightChild).name);
            } else if (rightChild instanceof AsNode) {
                tables.add(((IdentifierNode) ((AsNode) rightChild).value).name);
            }
        }
        else if (root instanceof AsNode) {
            tables.add(((IdentifierNode) ((AsNode) root).value).name);
        }
        else if (root instanceof IdentifierNode) {
            tables.add(((IdentifierNode) root).name);
        }
        return tables;
    }


    private Pair<String, String> getTableNameWithAlias(AsNode node) {
        Pair<String, String> tableWithAlias = null;
        if(node != null) {
            String alias = node.aliasName;
            String tableName = ((IdentifierNode) node.value).name;
            tableWithAlias = new Pair(alias, tableName);
        }

        return tableWithAlias;
    }

    private Map<String, String> alias(SelectNode select, String selectAlias,
                                      String tableName, String tableAlias) {
        Map<String, String> resolvedSubQColumns = new HashMap<>();

        BaseAstNode from = ((SelectNode) select).from;


        return resolvedSubQColumns;

    }




    private void dfs(BaseAstNode root, Set<BaseAstNode> visitedSubQueries) {
        ArrayStack<BaseAstNode> stack = new ArrayStack<>();

       // SelectNode select = (SelectNode) root;

        String subQAlias = null;

        String tableAlias = null;

        Map<String, String> resolvedSubQColumns = new HashMap<>();

        BaseAstNode from = ((SelectNode) root).from;
        stack.push(from);

        System.out.println("FROM =====>  " + from.getClass());

        while (!stack.isEmpty()) {
            BaseAstNode node = stack.pop();

            //System.out.println(node.toString());

            if (node instanceof SelectNode
                    && !visitedSubQueries.contains(node)) {
                //select = (SelectNode) node;
                stack.push(node);
            }
            else if (node instanceof AsNode) {
                AsNode asNode = (AsNode) node;

                String alias = asNode.aliasName;
                BaseAstNode value = asNode.value;

                if(value instanceof SelectNode) {
                    subQAlias = alias;
                } else if(value instanceof IdentifierNode) {
                    tableAlias = alias;
                }

                stack.push(value);
            }
            else if (node instanceof JoinNode) {
                JoinNode join = (JoinNode) node;

                BaseAstNode leftNode = join.leftNode;
                BaseAstNode rightNode = join.rightNode;

                stack.push(leftNode);
                stack.push(rightNode);
            }
            else if (node instanceof IdentifierNode) {
                String tableName = ((IdentifierNode) node).name;
                Map<String, String> resolvedSubQColumnsTemp = resolveColumnAlias(root, tableName,
                        tableAlias, subQAlias);

                if(tableAlias != null) {
                    resolvedSubQColumns.put(tableAlias, tableName);
                }

                resolveAlias(((SelectNode) root).where, tableName, resolvedSubQColumns);
                resolveAlias(((SelectNode) root).orderBy, tableName, resolvedSubQColumns);
                resolveAlias(((SelectNode) root).groupBy, tableName, resolvedSubQColumns);
                resolvedSubQColumns.putAll(resolvedSubQColumnsTemp);
            }
        }
    }


    private String getUnQualifiedName(String name) {
        if(name != null && name.contains(".")) {
            name = name.split(".")[1];
        }
        return name;
    }

    private String getFullyQualifiedName(IdentifierNode node, String tableName) {
        return getFullyQualifiedName(node.name, tableName);
    }

    private String getFullyQualifiedName(String columnName, String alias) {
        String name = columnName;

        if(name != null && name.contains(".")) {
            String toks[] = name.split(".");
            name = toks[1];
        }

        return String.format("%s.%s", alias, name);
    }


    private void resolveAlias(BaseAstNode root, String tableName,
                              Map<String, String> resolvedSubQColumns) {
        if(root != null && root instanceof BaseAstNodeList) {
            for(BaseAstNode node : ((BaseAstNodeList) root).getOperandlist()) {
                ArrayStack<BaseAstNode> stack = new ArrayStack<>();
                stack.push(node);

                while(!stack.isEmpty()) {
                    BaseAstNode next = stack.pop();

                    if(next instanceof IdentifierNode) {
                        IdentifierNode id = (IdentifierNode) next;
                        if(resolvedSubQColumns.containsKey(id.name)) {
                            id.name = resolvedSubQColumns.get(id.name);
                        }
                    } else if(!(next instanceof LiteralNode)) {
                        if(next instanceof BaseAstNodeList) {
                            for(BaseAstNode child : ((BaseAstNodeList) next).getOperandlist()) {
                                stack.push(child);
                            }
                        } else {
                            stack.push(next);
                        }
                    }
                }
            }
        }
    }


    private Map<String, String> resolveColumnAlias(BaseAstNode root, String tableName,
                              String tableAlias, String subQAlias) {
        Map<String, String> aliasResolvedColumns = new HashMap<>();

        BaseAstNodeList columns = ((SelectNode) root).columns;

        for(BaseAstNode column : columns.getOperandlist()) {
            String columnAlias = null;
            if(column instanceof AsNode) {
                AsNode asNode = (AsNode) column;
                columnAlias = asNode.aliasName;
                column = asNode.value;
            }

            if(column instanceof IdentifierNode) {
                IdentifierNode node = (IdentifierNode) column;
                String columnName = node.name;

                tableName = tableAlias != null ? tableAlias : tableName;
                String name = getFullyQualifiedName(node, tableName);
                // This is where we changed the column name
                node.name = name;

                if(subQAlias != null) {
                    String resolvedColumnName = getFullyQualifiedName(columnName, subQAlias);
                    aliasResolvedColumns.put(resolvedColumnName, name);
                }

                if(columnAlias != null) {
                    aliasResolvedColumns.put(columnAlias, name);
                } else {
                    aliasResolvedColumns.put(getUnQualifiedName(columnName), name);
                }
            }
        }

        return aliasResolvedColumns;
    }
}
