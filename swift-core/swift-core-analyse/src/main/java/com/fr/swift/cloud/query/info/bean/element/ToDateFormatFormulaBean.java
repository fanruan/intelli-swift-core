package com.fr.swift.cloud.query.info.bean.element;

import com.fr.swift.cloud.query.formula.FormulaType;

/**
 * @author yee
 * @date 2019-07-24
 */
public class ToDateFormatFormulaBean extends ToDateFormulaBean {
    private String format;

    public ToDateFormatFormulaBean(String formula, String timeColumn, String format) {
        super(formula, timeColumn);
        this.format = format;
        setType(FormulaType.TO_DATE_FORMAT);
    }

    public ToDateFormatFormulaBean(String formula, Long time, String format) {
        super(formula, time);
        this.format = format;
        setType(FormulaType.TO_DATE_FORMAT);
    }

    public ToDateFormatFormulaBean() {
        setType(FormulaType.TO_DATE_FORMAT);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
