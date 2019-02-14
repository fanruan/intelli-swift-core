package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/12/1.
 */
public class NumberInRangeFilterTest extends BaseFilterTest {

    public void test() {
        int min = 2;
        int max = 20;
        DetailFilter filter = new NumberInRangeFilter(min, max, false, false, intColumn, rowCount);
        List<Integer> expected = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (intDetails.get(i) != null && intDetails.get(i) > min && intDetails.get(i) < max) {
                expected.add(i);
            }
        }
        check(expected, filter.createFilterIndex());

        filter = new NumberInRangeFilter(min, max, true, false, intColumn, rowCount);
        expected = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (intDetails.get(i) != null && intDetails.get(i) >= min && intDetails.get(i) < max) {
                expected.add(i);
            }
        }
        check(expected, filter.createFilterIndex());

        filter = new NumberInRangeFilter(min, max, false, true, intColumn, rowCount);
        expected = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (intDetails.get(i) != null && intDetails.get(i) > min && intDetails.get(i) <= max) {
                expected.add(i);
            }
        }
        check(expected, filter.createFilterIndex());

        filter = new NumberInRangeFilter(min, max, true, true, intColumn, rowCount);
        expected = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (intDetails.get(i) != null && intDetails.get(i) >= min && intDetails.get(i) <= max) {
                expected.add(i);
            }
        }
        check(expected, filter.createFilterIndex());
    }

    public void testMatch() {
        DetailFilter filter = new NumberInRangeFilter(2, 3, true, false, intColumn, rowCount);
        SwiftNode node = new GroupNode();
        node.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(2)});
        assertTrue(filter.matches(node, 0, null));
        node.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(3)});
        assertFalse(filter.matches(node, 0, null));

        filter = new NumberInRangeFilter(2, 3, false, true, intColumn, rowCount);
        node.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(2)});
        assertFalse(filter.matches(node, 0, null));
        node.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(3)});
        assertTrue(filter.matches(node, 0, null));

        filter = new NumberInRangeFilter(2, 3, true, true, intColumn, rowCount);
        node.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(2)});
        assertTrue(filter.matches(node, 0, null));
        node.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(3)});
        assertTrue(filter.matches(node, 0, null));
    }

}
