package com.fr.bi.field.target.key.cal;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.general.ComparatorUtils;
import com.fr.script.Calculator;

import java.util.Map;


public class BIFormulaCalculatorTargetKey extends BICalculatorTargetKey {
    /**
     *
     */
    private static final long serialVersionUID = -1378991198704429207L;
    private static Calculator c = Calculator.createCalculator();
    private String expression;


    public BIFormulaCalculatorTargetKey(String expression, String targetName, Map targetMap) {
        super(targetName, targetMap);
        this.expression = expression;
    }

    @Override
    public int getSummaryType() {
        return BIReportConstant.SUMMARY_TYPE.SUM;
    }


    /**
     * hashֵ
     *
     * @return hashֵ
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + expression != null ? expression.hashCode() : 0;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BIFormulaCalculatorTargetKey other = (BIFormulaCalculatorTargetKey) obj;
        if (!ComparatorUtils.equals(expression, other.expression)) {
            return false;
        }
        return true;
    }


}
