package com.fr.swift.query.info.element.target.cal;

import com.fr.swift.query.info.bean.type.cal.CalTargetType;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GroupFormulaTarget that = (GroupFormulaTarget) o;

        return formula != null ? formula.equals(that.formula) : that.formula == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        return result;
    }
}
