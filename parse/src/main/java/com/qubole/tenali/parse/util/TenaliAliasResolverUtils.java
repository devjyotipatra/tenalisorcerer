package com.qubole.tenali.parse.util;

import com.qubole.tenali.parse.sql.datamodel.*;
import com.qubole.tenali.parse.sql.visitor.FunctionResolver;
import com.qubole.tenali.parse.sql.visitor.OperatorResolver;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TenaliAliasResolverUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TenaliAliasResolverUtils.class);


    public static TenaliAstNodeList resolveColumns(TenaliAstNode column,
                                               List<Triple<String, String, List<String>>> catalog,
                                               Map<String, Object> columnAliasMap) {
        TenaliAstNodeList columns = new TenaliAstNodeList();
        if(!(column instanceof TenaliAstNodeList)) {
            columns = columns.add(column);
        }

        return resolveColumns(columns, catalog, columnAliasMap);
    }


    public static TenaliAstNodeList resolveColumns(TenaliAstNodeList columns,
                                               List<Triple<String, String, List<String>>> catalog,
                                               Map<String, Object> columnAliasMap) {
        TenaliAstNodeList normalizedColumns = new TenaliAstNodeList();

        LOG.debug("ENTERING RESOLVE COLUMNS ..  " + catalog);
        Set<TenaliAstNode> resolvedColumns = new HashSet();
        Set<TenaliAstNode> unResolvedColumns = new HashSet();

        for(Triple<String, String, List<String>> cat : catalog) {
            String tableAlias = cat.getLeft();
            String tableName = cat.getMiddle();
            List<String> columnNames = cat.getRight();

            Map<String, String> tableColumns =  getColumnMap(tableName, columnNames);

            for (TenaliAstNode col : columns.getOperandlist()) {
                String name = null;
                String alias = null;
                TenaliAstNode unresColumn = col;

                if (col instanceof AsNode) {
                    AsNode t = (AsNode) col;
                    alias = t.aliasName;
                    unresColumn = t.value;
                }

                if (resolvedColumns.contains(unresColumn)
                        || unresColumn instanceof LiteralNode) {
                    continue;
                }

                name = getColumnName(unresColumn);
                Object resolvedColumn = null;

                if(unresColumn instanceof FunctionNode || unresColumn instanceof OperatorNode) {
                    if(unresColumn instanceof FunctionNode) {
                        resolvedColumn =  unresColumn.accept(new FunctionResolver(catalog, columnAliasMap));
                        addColumnAlias(alias, tableAlias, name,
                                ((FunctionNode) resolvedColumn).functionName, columnAliasMap);
                    } else {
                        resolvedColumn =  unresColumn.accept(new OperatorResolver(catalog, columnAliasMap));
                        addColumnAlias(alias, tableAlias, name,
                                ((OperatorNode) resolvedColumn).operator, columnAliasMap);
                    }

                } else if (columnAliasMap.containsKey(name)) {
                    resolvedColumn = columnAliasMap.get(name);
                } else if(name.contains(".")) {
                    int idx = name.lastIndexOf('.');
                    String aname = name.substring(0, idx);
                    String cname = name.substring(idx + 1, name.length());

                    if(columnAliasMap.containsKey(cname)) {
                        resolvedColumn = columnAliasMap.get(cname);
                    } else if((aname.equals(tableAlias) || aname.equals(tableName))) {
                        if(tableColumns.containsKey(cname)) {
                            resolvedColumn = tableColumns.get(cname);
                        } else if(tableName != null) {
                            resolvedColumn = tableName + "." + cname;
                        }
                    }
                } else if(tableColumns.containsKey(name)) {
                    resolvedColumn = tableColumns.get(name);
                }

                LOG.debug(" :  Lookup Name : " + name + " ::::  " +   " ResolvedName  " + resolvedColumn);

                if (resolvedColumn != null) {
                    resolvedColumns.add(unresColumn);

                    if(resolvedColumn instanceof TenaliAstNode) {
                        normalizedColumns.add((TenaliAstNode) resolvedColumn);
                    } else {
                        normalizedColumns.add(new IdentifierNode(resolvedColumn.toString()));
                    }
                } else {
                    unResolvedColumns.add(unresColumn);
                }

                addColumnAlias(alias, tableAlias, name, resolvedColumn, columnAliasMap);
            }
        }

        for(TenaliAstNode col : unResolvedColumns) {
            if (!resolvedColumns.contains(col)) {
                normalizedColumns.add(col);
            }
        }

        if(normalizedColumns.size() == 0) {
            normalizedColumns.add(columns);
        }

        LOG.debug("normalizedColumns =  " + normalizedColumns);

        return normalizedColumns;
    }


    private static void addColumnAlias(String alias, String tableAlias, String name,
                                Object resolvedColumn,
                                Map<String, Object> columnAliasMap) {
        Object aliasResolvedColumn = resolvedColumn != null ? resolvedColumn : name;

        if (alias != null) {
            columnAliasMap.put(alias, aliasResolvedColumn);

            if (tableAlias != null) {
                columnAliasMap.put(tableAlias + "." + alias, aliasResolvedColumn);
            }
        }
    }


    private static Map<String, String> getColumnMap(String tableName, List<String> columnNames) {
        Map<String, String> tableColumns = new HashMap();
        for(String column : columnNames) {
            if(column.contains(".")) {
                String[] tokens = column.split("\\.");
                tableColumns.put(tokens[tokens.length - 1], column);
            } else if(tableName != null) {
                tableColumns.put(column, tableName + "." + column);
            } else {
                tableColumns.put(column, column);
            }
        }

        return tableColumns;
    }


    private static String getColumnName(TenaliAstNode column) {
        String name = null;

        if(column instanceof IdentifierNode) {
            name = ((IdentifierNode) column).name;
        } else if(column instanceof FunctionNode) {
            name = ((FunctionNode) column).functionName;
        } else if(column instanceof OperatorNode) {
            name = ((OperatorNode) column).operator;
            if(name.length() == 1) {
                name = "TENALI_ARITHMATIC";
            }
        } else if(column instanceof LiteralNode) {
            name = "TENALI_LITERAL";
        }

        return name;
    }

}
