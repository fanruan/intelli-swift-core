package com.fr.swift.query.formula.impl;

import com.fr.swift.query.formula.FormulaType;
import com.fr.swift.segment.column.ColumnKey;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yee
 * @date 2019-07-24
 */
public class ToDateFormatFormula extends ToDateFormula {
    private String dateFormat;

    public ToDateFormatFormula(String formula, ColumnKey timeColumn, String dateFormat) {
        super(formula, timeColumn);
        this.dateFormat = dateFormat;
        setType(FormulaType.TO_DATE_FORMAT);
    }

    public ToDateFormatFormula(String formula, Long timestamp, String dateFormat) {
        super(formula, timestamp);
        this.dateFormat = dateFormat;
        setType(FormulaType.TO_DATE_FORMAT);
    }

    @Override
    public Object eval(Object... args) {
        Object eval = super.eval(args);
        return eval != null ? new SimpleDateFormat(dateFormat).format((Date) eval) : null;
    }
}
