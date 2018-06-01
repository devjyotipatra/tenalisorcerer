package com.qubole.tenali.parse.parser.config;

/**
 * Created by devjyotip on 5/31/18.
 */

import org.apache.calcite.sql.validate.SqlAbstractConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;

/**
 * Implementation of Calcite {@code SqlConformance} for Presto.
 */
public class TenaliConformance extends SqlAbstractConformance {
  private static final TenaliConformance INSTANCE = new TenaliConformance();

  public static TenaliConformance instance() {
    return INSTANCE;
  }

  @Override
  public boolean isBangEqualAllowed() {
    // For x != y (as an alternative to x <> y)
    return true;
  }

  @Override
  public boolean isSortByOrdinal() {
    // For ORDER BY 1
    return true;
  }

  @Override
  public boolean isSortByAlias() {
    // For ORDER BY columnAlias (where columnAlias is a "column AS columnAlias")
    return true;
  }

  @Override
  public boolean isGroupByAlias() {
    // Disable GROUP BY columnAlias (where columnAlias is a "column AS columnAlias")
    // since it causes ambiguity in the Calcite validator for queries like
    // SELECT func(x) AS x, COUNT(*) FROM table GROUP BY func(x)
    // Hive also does the same.
    return false;
  }

  @Override
  public boolean isGroupByOrdinal() {
    return true;
  }

  @Override
  public boolean isHavingAlias() {
    return true;
  }

  @Override
  public boolean isMinusAllowed() {
    return SqlConformanceEnum.DEFAULT.isMinusAllowed();
  }

  @Override
  public boolean isApplyAllowed() {
    return SqlConformanceEnum.DEFAULT.isApplyAllowed();
  }
}
