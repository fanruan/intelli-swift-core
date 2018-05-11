package com.fr.swift.query.adapter.dimension;

import com.fr.script.Calculator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.util.Crasher;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by pony on 2018/5/10.
 */
public class DetailFormulaDicColumn implements DictionaryEncodedColumn {
    private String formula;
    private Segment segment;
    private Calculator c = Calculator.createCalculator();
    private Map<String, ColumnKey> columnIndexMap;

    public DetailFormulaDicColumn(String formula, Segment segment) {
        this.formula = FormulaUtils.getParameterIndexEncodedFormula(formula);
        this.segment = segment;
        this.columnIndexMap = FormulaUtils.createColumnIndexMap(formula, segment);
    }

    @Override
    public void putSize(int size) {
        Crasher.crash("unsupported");
    }

    @Override
    public int size() {
        return Crasher.crash("unsupported");
    }

    @Override
    public void putGlobalSize(int globalSize) {
        Crasher.crash("unsupported");
    }

    @Override
    public int globalSize() {
        return Crasher.crash("unsupported");
    }

    @Override
    public void putValue(int index, Object val) {
        Crasher.crash("unsupported");
    }

    @Override
    public Object getValue(int index) {
        return Crasher.crash("unsupported");
    }

    @Override
    public Object getValueByRow(int row) {
        return FormulaUtils.getCalculatorValue(c, formula, segment, columnIndexMap, row);
    }

    @Override
    public int getIndex(Object value) {
        return Crasher.crash("unsupported");
    }

    @Override
    public void putIndex(int row, int index) {
        Crasher.crash("unsupported");
    }

    @Override
    public int getIndexByRow(int row) {
        return Crasher.crash("unsupported");
    }

    @Override
    public void putGlobalIndex(int index, int globalIndex) {
        Crasher.crash("unsupported");
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        return Crasher.crash("unsupported");
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        return Crasher.crash("unsupported");
    }

    @Override
    public Comparator getComparator() {
        return Crasher.crash("unsupported");
    }

    @Override
    public void flush() {
        Crasher.crash("unsupported");
    }

    @Override
    public void release() {
        Crasher.crash("unsupported");
    }
}
