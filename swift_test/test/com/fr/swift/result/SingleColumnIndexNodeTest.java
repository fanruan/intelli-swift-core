package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregateValue;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;

import java.util.Comparator;

/**
 * Created by pony on 2017/12/8.
 */
public class SingleColumnIndexNodeTest extends TestCase {

    SingleColumnIndexNode node;
    SingleColumnIndexNode child;
    SingleColumnIndexNode sibling;
    DictionaryEncodedColumn column;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final String[] keys = {"A","B","C"};
        final int[] index = {0,1,2,1,2,1,0,2,1};
        column =  new DictionaryEncodedColumn() {
            @Override
            public void flush() {

            }

            @Override
            public int size() {
                return 3;
            }

            @Override
            public void putGlobalSize(int globalSize) {

            }

            @Override
            public int globalSize() {
                return 0;
            }

            @Override
            public void putSize(int size) {

            }

            @Override
            public Object getValue(int index) {
                return keys[index];
            }

            @Override
            public void putValue(int index, Object val) {

            }

            @Override
            public int getIndex(Object value) {
                return 0;
            }

            @Override
            public void putIndex(int row, int index) {

            }

            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }

            @Override
            public void putGlobalIndex(int index, int globalIndex) {

            }

            @Override
            public int getGlobalIndexByRow(int row) {
                return 0;
            }

            @Override
            public int getGlobalIndexByIndex(int index) {
                return 0;
            }

            @Override
            public void release() {

            }

            @Override
            public Comparator getComparator() {
                return null;
            }
        };
        node = new SingleColumnIndexNode(1, 2, column);
        child = new SingleColumnIndexNode(1, 2, column);
        node.addChild(child);
        sibling = new SingleColumnIndexNode(1, 0, column);
        node.setSibling(sibling);
    }

    public void testGetData() {
        assertEquals(node.getData(), "C");
    }

    public void testGetChildrenSize() {
        assertEquals(node.getChildrenSize(), 1);
    }

    public void testGetIndex() {
        assertEquals(child.getIndex(), 0);
    }

    public void testGetSibling() {
        assertEquals(node.getSibling(), sibling);
    }

    public void testGetAggregatorValue() {
        AggregatorValue value = new DoubleAmountAggregateValue();
        node.setAggregatorValue(new TargetGettingKey(0), value);
        assertEquals(value, node.getAggregatorValue(new TargetGettingKey(0)));
        AggregatorValue value1 = new DoubleAmountAggregateValue();
        AggregatorValue[] values = new AggregatorValue[]{value1};
        node.setAggregatorValue(values);
        assertEquals(values, node.getAggregatorValue());

    }


}