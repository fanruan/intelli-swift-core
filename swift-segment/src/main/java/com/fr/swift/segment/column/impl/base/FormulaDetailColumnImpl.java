package com.fr.swift.segment.column.impl.base;

import com.fr.swift.query.formula.Formula;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.FormulaDetailColumn;
import com.fr.swift.util.Crasher;

//import com.fr.swift.source.etl.utils.FormulaUtils;

/**
 *
 * @author pony
 * @date 2018/5/11
 */
public class FormulaDetailColumnImpl implements FormulaDetailColumn {
    private Formula formula;
    private DetailColumn column;
    private Segment segment;
//    private Calculator c = Calculator.createCalculator();
//    private Map<String, ColumnKey> columnIndexMap;

    public FormulaDetailColumnImpl(Formula formula, Segment segment) {
        this.formula = formula;
//        this.formula = FormulaUtils.getParameterIndexEncodedFormula(formula);
//        this.segment = segment;
//        this.columnIndexMap = FormulaUtils.createColumnIndexMap(formula, segment);
        if (formula.getColumnKeys() != null) {
            column = segment.getColumn(formula.getColumnKeys()[0]).getDetailColumn();
        }
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
        if (null != column) {
            return formula.eval(column.get(pos));
        }
        return formula.eval();
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void release() {

    }
}
