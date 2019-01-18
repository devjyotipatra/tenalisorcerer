package com.qubole.tenali.parse.parser.config;

/**
 * Created by devjyotip on 5/31/18.
 */

import org.apache.calcite.sql.validate.SqlAbstractConformance;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;

/**
 * Implementation of Calcite {@code SqlConformance} for Presto.
 */
public class TenaliConformance implements SqlConformance {
    private TenaliConformance() { }

    private static final TenaliConformance INSTANCE = new TenaliConformance();

    public static TenaliConformance instance() {
    return INSTANCE;
  }


    public boolean isLiberal() {
        return SqlConformanceEnum.DEFAULT.isLiberal();
    }

    public boolean isGroupByAlias() {
        return false;
    }

    public boolean isGroupByOrdinal() {
        return true;
    }

    public boolean isHavingAlias() {
        return true;
    }

    public boolean isSortByOrdinal() {
        return true;
    }

    public boolean isSortByAlias() {
        return true;
    }

    public boolean isSortByAliasObscures() {
        return SqlConformanceEnum.DEFAULT.isSortByAliasObscures();
    }

    public boolean isFromRequired() {
        return SqlConformanceEnum.DEFAULT.isFromRequired();
    }

    public boolean isBangEqualAllowed() {
        return true;
    }

    public boolean isMinusAllowed() {
        return SqlConformanceEnum.DEFAULT.isMinusAllowed();
    }

    public boolean isApplyAllowed() {
        return SqlConformanceEnum.DEFAULT.isApplyAllowed();
    }

    public boolean isInsertSubsetColumnsAllowed() {
        return SqlConformanceEnum.DEFAULT.isInsertSubsetColumnsAllowed();
    }

    public boolean allowNiladicParentheses() {
        return SqlConformanceEnum.DEFAULT.allowNiladicParentheses();
    }

    public boolean allowExplicitRowValueConstructor() {
        return SqlConformanceEnum.DEFAULT.allowExplicitRowValueConstructor();
    }

    public boolean allowExtend() {
        return SqlConformanceEnum.DEFAULT.allowExtend();
    }

    public boolean isLimitStartCountAllowed() {
        return SqlConformanceEnum.DEFAULT.isLimitStartCountAllowed();
    }

    public boolean isPercentRemainderAllowed() {
        return SqlConformanceEnum.DEFAULT.isPercentRemainderAllowed();
    }

    public boolean allowGeometry() {
        return SqlConformanceEnum.DEFAULT.allowGeometry();
    }

    public boolean shouldConvertRaggedUnionTypesToVarying() {
        return SqlConformanceEnum.DEFAULT.shouldConvertRaggedUnionTypesToVarying();
    }


}
