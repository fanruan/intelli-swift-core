package com.fr.swift.adaptor.widget.target;

import com.fr.swift.query.aggregator.AggregatorType;

/**
 * Created by pony on 2018/5/21.
 */
public class AggUnit {
    private AggregatorType aggregatorType;
    private String formula;

    public AggUnit(AggregatorType aggregatorType, String formula) {
        this.aggregatorType = aggregatorType;
        this.formula = formula;
    }

    public AggregatorType getAggregatorType() {
        return aggregatorType;
    }

    public String getFormula() {
        return formula;
    }

    public String toAGGString() {
        return getAGGString(aggregatorType) + AggFormulaUtils.AGG_REG + "(" + formula + ")";
    }

    private String getAGGString(AggregatorType type) {
        switch (type) {
            case MIN:
                return "MIN";
            case COUNT:
                return "COUNT";
            case MAX:
                return "MAX";
            case MEDIAN:
                return "MEDIAN";
            case DISTINCT:
                return "COUNTD";
            case VARIANCE:
                return "VAR";
            case AVERAGE:
                return "AVG";
            case STANDARD_DEVIATION:
                return "STADEV";
            case SUM:
                return "SUM";
            default:
                return "SUM";
        }
    }
}
