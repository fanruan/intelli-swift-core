package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.formula.FormulaType;

/**
 * @author yee
 * @date 2019-07-24
 */
public class ToDateFormulaBean extends BaseFormulaBean {
    private Long time;

    public ToDateFormulaBean(String formula, String timeColumn) {
        super(formula, new String[]{timeColumn});
        setType(FormulaType.TO_DATE);
    }

    public ToDateFormulaBean(String formula, Long time) {
        this.time = time;
        setFormula(formula);
        setType(FormulaType.TO_DATE);
    }

    public ToDateFormulaBean() {
        setType(FormulaType.TO_DATE);
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
