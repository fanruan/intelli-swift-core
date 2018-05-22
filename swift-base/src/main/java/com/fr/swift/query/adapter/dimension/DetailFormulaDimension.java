package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2018/5/10.
 */
public class DetailFormulaDimension extends DetailDimension {
    private String formula;

    public DetailFormulaDimension(int index, SourceKey sourceKey,  FilterInfo filterInfo, String formula) {
        super(index, sourceKey, null, null, new NoneSort(), filterInfo);
        this.formula = formula;
    }

    @Override
    public Column getColumn(Segment segment) {
        return new DetailFormulaColumn(formula, segment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DetailFormulaDimension that = (DetailFormulaDimension) o;

        return formula != null ? formula.equals(that.formula) : that.formula == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        return result;
    }
}
