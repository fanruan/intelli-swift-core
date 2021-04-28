package com.fr.swift.cloud.query.info.element.dimension;

import com.fr.swift.cloud.query.formula.Formula;
import com.fr.swift.cloud.query.group.info.IndexInfoImpl;
import com.fr.swift.cloud.query.info.bean.type.DimensionType;
import com.fr.swift.cloud.query.sort.NoneSort;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.impl.base.DetailFormulaColumn;

/**
 * Created by pony on 2018/5/10.
 */
public class DetailFormulaDimension extends DetailDimension {
    //    private String formula;
    private Formula formula;

    public DetailFormulaDimension(int index, Formula formula) {
        super(index, null, null, new NoneSort(), new IndexInfoImpl(false, false));
        this.formula = formula;
    }

//    public String getFormula() {
//        return formula;
//    }

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

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.DETAIL_FORMULA;
    }
}
