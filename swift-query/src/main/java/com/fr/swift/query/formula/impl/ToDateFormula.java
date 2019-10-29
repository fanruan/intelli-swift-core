package com.fr.swift.query.formula.impl;

import com.fr.swift.query.formula.FormulaType;
import com.fr.swift.segment.column.ColumnKey;

import java.util.Date;

/**
 * @author yee
 * @date 2019-07-24
 */
public class ToDateFormula extends BaseFormula {

    private ColumnKey timeColumn;
    private Long timestamp;

    public ToDateFormula(String formula, ColumnKey timeColumn) {
        super(formula, FormulaType.TO_DATE);
        this.timeColumn = timeColumn;
    }

    public ToDateFormula(String formula, Long timestamp) {
        super(formula, FormulaType.TO_DATE);
        this.timestamp = timestamp;
    }

    @Override
    public ColumnKey[] getColumnKeys() {
        if (null != timeColumn) {
            return new ColumnKey[]{timeColumn};
        }
        return null;
    }

    @Override
    public Object eval(Object... args) {
        // 常数
        if (null != timestamp) {
            return new Date(timestamp);
        }
        // 字段
        if (args != null && args.length >= 1) {
            try {
                Long arg = (Long) args[0];
                return null == arg ? null : new Date(arg);
            } catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }
}
