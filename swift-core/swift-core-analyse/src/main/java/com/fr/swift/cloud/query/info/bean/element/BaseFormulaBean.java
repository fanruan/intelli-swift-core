package com.fr.swift.cloud.query.info.bean.element;

import com.fr.swift.cloud.query.formula.FormulaType;

/**
 * @author yee
 * @date 2019-07-24
 */
public class BaseFormulaBean implements FormulaBean {
    private String formula;
    private String[] columns;
    private FormulaType type;

    public BaseFormulaBean(String formula, String[] columns) {
        this.formula = formula;
        this.columns = columns;
    }

    public BaseFormulaBean() {
    }

    @Override
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    @Override
    public FormulaType getType() {
        return type;
    }

    public void setType(FormulaType type) {
        this.type = type;
    }
}
