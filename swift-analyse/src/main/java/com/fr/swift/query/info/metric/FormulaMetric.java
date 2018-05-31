package com.fr.swift.query.info.metric;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.base.FormulaMetricColumn;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2018/5/10.
 */
public class FormulaMetric extends GroupMetric{
    private String formula;
    public FormulaMetric(int queryIndex, SourceKey sourceKey,  FilterInfo filterInfo, Aggregator aggregator, String formula) {
        super(queryIndex, sourceKey, null, filterInfo, aggregator);
        this.formula = formula;
    }

    @Override
    public Column getColumn(Segment segment) {
        return new FormulaMetricColumn(formula, segment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FormulaMetric that = (FormulaMetric) o;

        return formula != null ? formula.equals(that.formula) : that.formula == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        return result;
    }
}
