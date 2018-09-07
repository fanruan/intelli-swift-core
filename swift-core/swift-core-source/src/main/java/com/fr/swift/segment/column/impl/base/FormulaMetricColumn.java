package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.util.Crasher;

/**
 * Created by pony on 2018/5/11.
 */
public class FormulaMetricColumn implements Column {
    private String formula;
    private Segment segment;

    public FormulaMetricColumn(String formula, Segment segment) {
        this.formula = formula;
        this.segment = segment;
    }

    @Override
    public DictionaryEncodedColumn getDictionaryEncodedColumn() {
        return new DetailFormulaDicColumn(formula, segment);
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new FormulaIndexColumn(formula, segment);
    }

    @Override
    public DetailColumn getDetailColumn() {
        return new FormulaDetailColumnImpl(formula, segment);
    }

    @Override
    public IResourceLocation getLocation() {
        return Crasher.crash("unsupported");
    }
}
