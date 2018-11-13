package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.expect;

/**
 * Created by Lyon on 2018/7/13.
 */
public class HLLDistinctAggregatorTest extends TestCase {

    public void testAggregate() {

        RowTraversal traversal = AllShowBitMap.of(5);
        IMocksControl control = EasyMock.createControl();
        Column column = control.createMock(Column.class);
        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        DetailColumn dic = control.createMock(DetailColumn.class);

        expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.STRING).anyTimes();
        expect(column.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();
        expect(column.getDetailColumn()).andReturn(dic).anyTimes();
        expect(dic.get(0)).andReturn("a").anyTimes();
        expect(dic.get(1)).andReturn("b").anyTimes();
        expect(dic.get(2)).andReturn("c").anyTimes();
        expect(dic.get(3)).andReturn("a").anyTimes();
        expect(dic.get(4)).andReturn("b").anyTimes();

        control.replay();

        double count = 3;
        Aggregator<HLLAggregatorValue> aggregator = HLLDistinctAggregator.INSTANCE;
        HLLAggregatorValue value = aggregator.aggregate(traversal, column);
        assertEquals(count, value.calculate());
    }

    public void testHighCardinality() {

        IMocksControl control = EasyMock.createControl();
        Column column = control.createMock(Column.class);
        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        DetailColumn dic = control.createMock(DetailColumn.class);

        expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.INTEGER).anyTimes();
        expect(column.getDetailColumn()).andReturn(dic).anyTimes();
        expect(column.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();
        int size = 1000;
        RowTraversal traversal = AllShowBitMap.of(10000);
        for (int i = 0; i < 10000; i++) {
            expect(dic.getInt(i)).andReturn(i % size);
        }
        control.replay();
        Aggregator<HLLAggregatorValue> aggregator = HLLDistinctAggregator.INSTANCE;
        long start = System.currentTimeMillis();
        HLLAggregatorValue value = aggregator.aggregate(traversal, column);
        double estimate = value.calculate();
        System.out.println("time: " + (System.currentTimeMillis() - start));
        double err = Math.abs(estimate - size) / (double) size;
        System.out.println(err);
        assertTrue(err < .01);
    }

    public void testCombine() {

        RowTraversal traversal = AllShowBitMap.of(5);
        IMocksControl control = EasyMock.createControl();
        Column column = control.createMock(Column.class);
        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        DetailColumn dic = control.createMock(DetailColumn.class);

        expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.STRING).anyTimes();
        expect(column.getDetailColumn()).andReturn(dic).anyTimes();
        expect(column.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();
        expect(dic.get(0)).andReturn("a").anyTimes();
        expect(dic.get(1)).andReturn("b").anyTimes();
        expect(dic.get(2)).andReturn("c").anyTimes();
        expect(dic.get(3)).andReturn("a").anyTimes();
        expect(dic.get(4)).andReturn("b").anyTimes();
        control.replay();
        Aggregator<HLLAggregatorValue> aggregator = HLLDistinctAggregator.INSTANCE;
        HLLAggregatorValue value = aggregator.aggregate(traversal, column);

        RowTraversal traversal1 = AllShowBitMap.of(5);
        IMocksControl control1 = EasyMock.createControl();
        Column column1 = control1.createMock(Column.class);
        DictionaryEncodedColumn dictionaryEncodedColumn1 = control1.createMock(DictionaryEncodedColumn.class);
        DetailColumn dic1 = control1.createMock(DetailColumn.class);
        expect(dictionaryEncodedColumn1.getType()).andReturn(ColumnTypeConstants.ClassType.STRING).anyTimes();
        expect(column1.getDetailColumn()).andReturn(dic1).anyTimes();
        expect(column1.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn1).anyTimes();
        expect(dic1.get(0)).andReturn("e").anyTimes();
        expect(dic1.get(1)).andReturn("f").anyTimes();
        expect(dic1.get(2)).andReturn("f").anyTimes();
        expect(dic1.get(3)).andReturn("g").anyTimes();
        expect(dic1.get(4)).andReturn("g").anyTimes();
        control1.replay();
        Aggregator<HLLAggregatorValue> aggregator1 = HLLDistinctAggregator.INSTANCE;
        HLLAggregatorValue value1 = aggregator1.aggregate(traversal1, column1);

        HLLDistinctAggregator.INSTANCE.combine(value, value1);

        double count = value.calculate();
        assertTrue(count == 6);
    }
}
