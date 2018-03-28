package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;


import java.util.Arrays;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;

/**
 * @author Xiaolei.liu
 */

public class MedianAggregate extends AbstractAggregator<MedianAggregatorValue>{

    protected static final Aggregator INSTANCE = new MedianAggregate();

    @Override
    public MedianAggregatorValue aggregate(RowTraversal traversal, Column column) {

        final MedianAggregatorValue valueAmount = new MedianAggregatorValue();
        final DictionaryEncodedColumn diColumn = column.getDictionaryEncodedColumn();
        final int[] groupIndex = new int[diColumn.size()];
        Arrays.fill(groupIndex, 0);
        int mid = (traversal.getCardinality() / 2) + 1;
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()){
            return new MedianAggregatorValue();
        }

//        if (traversal.isEmpty()) {
//            valueAmount.setMedian(NULL_DOUBLE);
//            return valueAmount;
//        }
        notNullTraversal.traversal(new CalculatorTraversalAction(){

            @Override
            public double getCalculatorValue() {
                return 0;
            }

            @Override
            public void actionPerformed(int row) {

                int groupRow = diColumn.getIndexByRow(row);
                groupIndex[groupRow] ++;
            }
        });
        //偶数时中间两数的前一个值
        double tempMid = NULL_DOUBLE;
        for(int i = 1; i < diColumn.size(); i++) {
            mid -= groupIndex[i];
            if(Double.compare(tempMid, NULL_DOUBLE) != 0) {
                valueAmount.setMedian(((Double)diColumn.getValue(i) + tempMid) / 2);
                return valueAmount;
            }
            if(mid <= 0) {
                valueAmount.setMedian((Double)diColumn.getValue(i));
                return valueAmount;
            }
            if(traversal.getCardinality() % 2 == 0 && mid == 1) {
                tempMid = (Double)diColumn.getValue(i);
            }
        }
        return valueAmount;
    }


    @Override
    public void combine(MedianAggregatorValue value, MedianAggregatorValue other) {
        //
    }

}
