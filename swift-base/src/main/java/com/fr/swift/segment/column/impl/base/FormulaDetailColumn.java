package com.fr.swift.segment.column.impl.base;

import com.fr.script.Calculator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.util.Crasher;

import java.util.Map;

/**
 * Created by pony on 2018/5/11.
 */
public class FormulaDetailColumn implements DetailColumn {
    private String formula;
    private Segment segment;
    private Calculator c = Calculator.createCalculator();
    private Map<String, ColumnKey> columnIndexMap;

    public FormulaDetailColumn(String formula, Segment segment) {
        this.formula = FormulaUtils.getParameterIndexEncodedFormula(formula);
        this.segment = segment;
        this.columnIndexMap = FormulaUtils.createColumnIndexMap(formula, segment);
    }
    @Override
    public int getInt(int pos) {
        return Crasher.crash("unsupported");
    }

    @Override
    public long getLong(int pos) {
        return Crasher.crash("unsupported");
    }

    @Override
    public double getDouble(int pos) {
        Object value = get(pos);
        return value instanceof Number ? ((Number) value).doubleValue() : 0;
    }

    @Override
    public void put(int pos, Object val) {
        Crasher.crash("unsupported");
    }

    @Override
    public Object get(int pos) {
        return FormulaUtils.getCalculatorValue(c, formula, segment, columnIndexMap, pos);
    }

    @Override
    public void flush() {

    }

    @Override
    public void release() {

    }
}
