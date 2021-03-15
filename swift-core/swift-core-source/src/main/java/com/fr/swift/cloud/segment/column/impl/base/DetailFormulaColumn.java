package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.query.formula.Formula;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.util.Crasher;

/**
 * Created by pony on 2018/5/10.
 */
public class DetailFormulaColumn implements Column {
    //    private String formula;
    private Segment segment;
    private Formula formula;

    public DetailFormulaColumn(Formula formula, Segment segment) {
        this.formula = formula;
        this.segment = segment;
    }


    @Override
    public DictionaryEncodedColumn getDictionaryEncodedColumn() {
        return new DetailFormulaDicColumn(formula, segment);
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return Crasher.crash("unsupported");
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
