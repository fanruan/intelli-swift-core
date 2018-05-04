package com.fr.swift.query.adapter.target.cal;

/**
 * Created by Lyon on 2018/5/3.
 */
public class GroupFormulaTarget extends GroupTargetImpl {

    private String formula;

    public GroupFormulaTarget(int queryIndex, int resultIndex, int[] paramIndexes, CalTargetType type, String formula) {
        super(queryIndex, resultIndex, paramIndexes, type);
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }
}
