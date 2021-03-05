package com.fr.swift.cloud.query.filter;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.segment.Segment;

/**
 * Created by Lyon on 2018/3/22.
 */
public class FormulaFilter implements DetailFilter {

    private String expression;
    private Segment segment;

    public FormulaFilter(String expression, Segment segment) {
        this.expression = expression;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
//        Calculator calculator = Calculator.createCalculator();
//        String formula = FormulaUtils.getParameterIndexEncodedFormula(expression);
//        Map<String, ColumnKey> columnKeyMap = FormulaUtils.createColumnIndexMap(expression, segment);
        MutableBitMap bitMap = BitMaps.newRoaringMutable();
//        for (int row = 0, rowCount = segment.getRowCount(); row < rowCount; row++) {
//            Object value = FormulaUtils.getCalculatorValue(calculator, formula, segment, columnKeyMap, row);
//            if (value instanceof Boolean && (Boolean) value) {
//                bitMap.add(row);
//            }
//        }
        return bitMap.toBitMap();
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//        Calculator calculator = Calculator.createCalculator();
//        String formula = FormulaUtils.getParameterIndexEncodedFormula(expression);
//        Map<String, Integer> map = FormulaUtils.createColumnIndexMap(expression);
//        Object value = FormulaUtils.getCalculatorValue(calculator, formula, node, map);
//        if (value instanceof Boolean && (Boolean) value) {
//            return true;
//        }
        return false;
    }
}
