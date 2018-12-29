package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * @author Xiaolei.liu
 */

public class MedianAggregate extends AbstractAggregator<MedianAggregatorValue> {

    protected static final Aggregator INSTANCE = new MedianAggregate();
    private static final long serialVersionUID = -3176813043384218661L;

    @Override
    public MedianAggregatorValue aggregate(RowTraversal traversal, Column column) {

        final MedianAggregatorValue valueAmount = new MedianAggregatorValue();
        final DictionaryEncodedColumn diColumn = column.getDictionaryEncodedColumn();
        final int[] groupIndex = new int[diColumn.size()];
        TreeMap<Double, Integer> values = new TreeMap<Double, Integer>();
        Arrays.fill(groupIndex, 0);
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()) {
            return valueAmount;
        }
        valueAmount.setCount(notNullTraversal.getCardinality());
        notNullTraversal.traversal(new CalculatorTraversalAction() {

            @Override
            public double getCalculatorValue() {
                return 0;
            }

            @Override
            public void actionPerformed(int row) {
                int groupRow = diColumn.getIndexByRow(row);
                groupIndex[groupRow]++;
            }
        });
        //求中位数除去空值
        setMedian(values, diColumn, notNullTraversal.getCardinality(), groupIndex, valueAmount);
        valueAmount.setValues(values);
        return valueAmount;
    }


    @Override
    public void combine(MedianAggregatorValue value, MedianAggregatorValue other) {
        int totalCount = value.getCount() + other.getCount();
        int mid = totalCount / 2 + 1;
        TreeMap<Double, Integer> vMap = value.getValues();
        TreeMap<Double, Integer> oMap = other.getValues();
        mergeMap(vMap, oMap);
        //偶数时中间两数的前一个值
        double tempMid = NULL_DOUBLE;
        for (Object o : vMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if (Double.compare(tempMid, NULL_DOUBLE) != 0) {
                value.setMedian(((Double) entry.getKey() + tempMid) / 2);
                break;
            }
            mid -= (Integer) entry.getValue();
            if (mid <= 0) {
                value.setMedian((Double) entry.getKey());
                break;
            }
            if (mid == 1 && (totalCount) % 2 == 0) {
                tempMid = (Double) entry.getKey();
            }
        }
        value.setCount(totalCount);
        value.setValues(vMap);
    }

    private void setMedian(Map<Double, Integer> values, DictionaryEncodedColumn diColumn, int count, int[] groupIndex, MedianAggregatorValue valueAmount) {
        double tempMid = NULL_DOUBLE;
        boolean getMedian = false;
        int mid = count / 2 + 1;
        for (int i = 1; i < diColumn.size(); i++) {
            if (groupIndex[i] <= 0) {
                continue;
            }
            values.put(((Number) diColumn.getValue(i)).doubleValue(), groupIndex[i]);
            mid -= groupIndex[i];
            if (!getMedian && Double.compare(tempMid, NULL_DOUBLE) != 0) {
                valueAmount.setMedian((((Number) diColumn.getValue(i)).doubleValue() + tempMid) / 2);
                getMedian = true;
            }
            if (!getMedian && mid <= 0) {
                valueAmount.setMedian(((Number) diColumn.getValue(i)).doubleValue());
                getMedian = true;
            }
            if (count % 2 == 0 && mid == 1) {
                tempMid = ((Number) diColumn.getValue(i)).doubleValue();
            }
        }
    }

    private void mergeMap(Map<Double, Integer> vMap, Map<Double, Integer> oMap) {
        if (vMap == null) {
            vMap = oMap;
        }
        if (oMap != null) {
            for (Object o : oMap.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object key = entry.getKey();
                if (vMap.containsKey(key)) {
                    Integer count = vMap.get(key) + oMap.get(key);
                    vMap.put((Double) key, count);
                } else {
                    vMap.put((Double) key, oMap.get(key));
                }
            }
        }
    }

    @Override
    public MedianAggregatorValue createAggregatorValue(AggregatorValue value) {
        if (value.calculateValue() == null) {
            return new MedianAggregatorValue();
        }
        MedianAggregatorValue medianAggregatorValue = new MedianAggregatorValue();
        medianAggregatorValue.setCount(1);
        TreeMap<Double, Integer> values = new TreeMap<Double, Integer>();
        values.put(value.calculate(), 1);
        medianAggregatorValue.setValues(values);
        medianAggregatorValue.setMedian(value.calculate());
        return medianAggregatorValue;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.MEDIAN;
    }
}
