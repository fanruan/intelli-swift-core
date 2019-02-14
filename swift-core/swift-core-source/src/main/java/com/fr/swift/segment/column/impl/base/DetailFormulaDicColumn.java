package com.fr.swift.segment.column.impl.base;

//import com.fr.script.Calculator;
//import com.fr.stable.Primitive;
//import com.fr.stable.UtilEvalError;
import com.fr.swift.compare.Comparators;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
//import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.util.Crasher;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by pony on 2018/5/10.
 */
public class DetailFormulaDicColumn implements DictionaryEncodedColumn {
    private String formula;
    private Segment segment;
    //    private Calculator c = Calculator.createCalculator();
    private Map<String, ColumnKey> columnIndexMap;
    private DictionaryEncodedColumn hostColumn;

    public DetailFormulaDicColumn(String formula, Segment segment) {
////        this.formula = FormulaUtils.getParameterIndexEncodedFormula(formula);
////        this.segment = segment;
////        this.columnIndexMap = FormulaUtils.createColumnIndexMap(formula, segment);
////        String[] paras = FormulaUtils.getRelatedParaNames(formula);
//        //todo 先取一个用到的列暂时用下，如果一个都没用到，就
//        if (paras.length != 0) {
//            hostColumn = segment.getColumn(new ColumnKey(paras[0])).getDictionaryEncodedColumn();
//        }
    }

    @Override
    public int size() {
        return hostColumn == null ? 1 : hostColumn.size();
    }

    @Override
    public int globalSize() {
        return hostColumn == null ? 1 : hostColumn.globalSize();
    }

    @Override
    public Object getValue(int index) {
        //todo 这个是错的，只适用于一个字段的，看来中位数还得生成啊
//        Iterator<Map.Entry<String, ColumnKey>> iter = columnIndexMap.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry<String, ColumnKey> entry = iter.next();
//            String columnName = entry.getKey();
//            ColumnKey columnKey = entry.getValue();
//            if (columnKey != null && hostColumn != null) {
//                Object value = hostColumn.getValue(index);
//                c.set(columnName, value);
//            } else {
//                c.remove(columnName);
//            }
//        }
//        try {
//            Object ob = c.eval(formula);
//            return ob == Primitive.NULL ? null : ob;
//        } catch (UtilEvalError e) {
//            return null;
//        }
        return null;
    }

    @Override
    public Object getValueByRow(int row) {
        return null;
//        return FormulaUtils.getCalculatorValue(c, formula, segment, columnIndexMap, row);
    }

    @Override
    public int getIndex(Object value) {
        return hostColumn == null ? 1 : hostColumn.getIndex(value);
    }

    @Override
    public int getIndexByRow(int row) {
        return hostColumn == null ? 1 : hostColumn.getIndexByRow(row);
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        return Crasher.crash("unsupported");
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        return hostColumn == null ? 1 : hostColumn.getGlobalIndexByRow(row);
    }

    @Override
    public Comparator getComparator() {
        return Comparators.asc();
    }

    @Override
    public ColumnTypeConstants.ClassType getType() {
        return hostColumn.getType();
    }

    @Override
    public Putter putter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        Crasher.crash("unsupported");
    }

    @Override
    public void release() {
        Crasher.crash("unsupported");
    }

    @Override
    public boolean isReadable() {
        return hostColumn != null && hostColumn.isReadable();
    }
}
