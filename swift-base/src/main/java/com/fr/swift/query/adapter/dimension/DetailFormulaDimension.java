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
}
