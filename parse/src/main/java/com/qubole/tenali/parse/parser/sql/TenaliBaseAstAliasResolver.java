package com.qubole.tenali.parse.parser.sql;

import com.qubole.tenali.metastore.APIMetastoreClient;
import com.qubole.tenali.metastore.CachingMetastoreClient;
import com.qubole.tenali.parse.parser.sql.datamodel.*;
import org.apache.calcite.util.Pair;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import scala.collection.mutable.ArrayStack;

import java.util.*;


public class TenaliBaseAstAliasResolver {

    Map<String, String> columnAlias = new HashMap<>();
    Map<String, String> tableAlias = new HashMap<>();
    Map<String, String> subqueryAlias = new HashMap<>();


    public TenaliBaseAstAliasResolver() { }


 /*   public void dfsFindAlias(BaseAstNode root) {

        Set<BaseAstNode> visitedSubQueries = new HashSet<>();

        if (root instanceof SelectNode) {
            dfsSelect(root, visitedSubQueries);
        }
    }



    private void dfsSelect(BaseAstNode root, Set<BaseAstNode> visitedSubQueries) {
        Stack<SelectNode> subQStack = new Stack<>();

        Stack<String> aliasStack = new Stack<>();

        Map<String, String> resolvedSubQColumns = new HashMap<>();

        subQStack.push((SelectNode) root);
        aliasStack.push("#NONE");


        while (!subQStack.isEmpty()) {
            BaseAstNode node = subQStack.peek();

            BaseAstNode from = ((SelectNode) node).from;

            List<Pair<String, String>> tables = getTables(from);

            switch (tables.size()) {
                case 0:
                    if (from instanceof AsNode) {
                        AsNode asNode = (AsNode) from;

                        pushSubQAndAlias(asNode, subQStack, aliasStack);
                    } else if (from instanceof JoinNode) {
                        BaseAstNode leftChild = ((JoinNode) root).leftNode;
                        BaseAstNode rightChild = ((JoinNode) root).rightNode;

                        if (leftChild instanceof AsNode) {
                            AsNode asNode = (AsNode) leftChild;
                            pushSubQAndAlias(asNode, subQStack, aliasStack);
                        } else {
                            subQStack.push((SelectNode) leftChild);
                            aliasStack.push("#NONE");
                        }

                        if (rightChild instanceof AsNode) {
                            AsNode asNode = (AsNode) rightChild;
                            pushSubQAndAlias(asNode, subQStack, aliasStack);
                        } else {
                            subQStack.push((SelectNode) rightChild);
                            aliasStack.push("#NONE");
                        }

                    } else if (from instanceof SelectNode) {
                        subQStack.push((SelectNode) from);
                        aliasStack.push("#NONE");
                    }
                    break;
                case 1:
                case 2:
                    System.out.println(tables);
                    SelectNode select = subQStack.pop();
                    String subQalias = aliasStack.pop();
                    resolveAlias(select, subQalias, tables);
                    break;

            }
        }
    }

    private void pushSubQAndAlias(AsNode asNode, Stack subQStack, Stack aliasStack) {
        String alias = asNode.aliasName;
        BaseAstNode valNode = asNode.value;

        if (valNode instanceof SelectNode) {
            subQStack.push((SelectNode) valNode);
            aliasStack.push(alias);
        }
    }

    private boolean isJoinTable(BaseAstNode child) {
        if (child == null ) {
            return false;
        }

        if(child instanceof AsNode) {
            BaseAstNode node = ((AsNode) child).value;
            if(node instanceof SelectNode) {
                return false;
            }
        }
        else if (child instanceof SelectNode) {
            return false;
        }

        return true;
    }


    private List<Pair<String, String>> getTables(BaseAstNode root) {
        List<Pair<String, String>> tables = new ArrayList<>();

        if(root instanceof JoinNode) {
            BaseAstNode leftChild = ((JoinNode) root).leftNode;
            BaseAstNode rightChild = ((JoinNode) root).rightNode;

            if(!(isJoinTable(leftChild) && isJoinTable(rightChild))) {
                return tables;
            }

            if(leftChild instanceof IdentifierNode) {
                tables.add(new Pair(null, ((IdentifierNode) leftChild).name));
            } else if (leftChild instanceof AsNode) {
                tables.add(new Pair(((AsNode) leftChild).aliasName,
                        ((IdentifierNode) ((AsNode) leftChild).value).name));
            }

            if(rightChild instanceof IdentifierNode) {
                tables.add(new Pair(null, ((IdentifierNode) rightChild).name));
                tables.add(new Pair(((AsNode) rightChild).aliasName,
                        ((IdentifierNode) ((AsNode) rightChild).value).name));
            }
        }
        else if (root instanceof AsNode) {
            BaseAstNode valNode = ((AsNode) root).value;

            if (valNode instanceof IdentifierNode) {
                tables.add(new Pair(((AsNode) root).aliasName, ((IdentifierNode) valNode).name));
            }
        }
        else if (root instanceof IdentifierNode) {
            tables.add(new Pair(null, ((IdentifierNode) root).name));
        }
        return tables;
    }

    public void resolveAlias(SelectNode root, String innerQueryAlias,
                             List<Pair<String, String>> tables) {
        Deque<BaseAstNode> queue = new ArrayDeque<>();
        queue.push(root.columns);
        queue.push(root.groupBy);
        queue.push(root.orderBy);
        queue.push(root.having);
        queue.push(root.keywords);
        queue.push(root.where);
        queue.push(root.windowDecls);

        while(!queue.isEmpty()) {
            BaseAstNode node = queue.pop();

            if(node != null) {
                if (node instanceof IdentifierNode) {
                    IdentifierNode id = (IdentifierNode) node;
                    String name = id.name;
                } else if(node instanceof AsNode) {
                    BaseAstNode n = ((AsNode) node).value;
                    queue.push(n);
                } else if (node instanceof BaseAstNodeList) {
                    for (BaseAstNode n : ((BaseAstNodeList) node).getOperandlist()) {
                        if(n != null) {
                            queue.push(node);
                        }
                    }
                } else if(node instanceof OperatorNode) {
                    BaseAstNode operands = ((OperatorNode) node).operands;
                    queue.push(operands);
                } else if(node instanceof FunctionNode) {
                    BaseAstNode operands = ((FunctionNode) node).arguments;
                    queue.push(operands);
                }
            }
        }
    }


    public void findAlias(String columnName, List<Pair<String, String>> tables,
                          String innerQueryAlias) {

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


   /* private Map<String, String> resolveColumnAlias(BaseAstNode root, String tableName,
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
    }*/
}
