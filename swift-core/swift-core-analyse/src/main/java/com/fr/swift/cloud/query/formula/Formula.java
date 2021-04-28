package com.fr.swift.cloud.query.formula;

import com.fr.swift.cloud.segment.column.ColumnKey;

/**
 * TODO 专门针对sql做的，这个等fr公式开源后需要修改
 *
 * @author yee
 * @date 2019-07-24
 */
public interface Formula {
    ColumnKey[] getColumnKeys();

    String getFormula();

    Object eval(Object... args);

    FormulaType getType();
}
