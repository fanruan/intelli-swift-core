package com.fr.swift.cloud.query.formula.impl;

import com.fr.swift.cloud.query.formula.Formula;
import com.fr.swift.cloud.query.formula.FormulaType;

/**
 * @author yee
 * @date 2019-07-24
 */
public abstract class BaseFormula implements Formula {
    private String formula;
    private FormulaType type;

    public BaseFormula(String formula, FormulaType type) {
        this.formula = formula;
        this.type = type;
    }

    @Override
    public String getFormula() {
        return formula;
    }

    @Override
    public FormulaType getType() {
        return type;
    }

    public void setType(FormulaType type) {
        this.type = type;
    }
}
