package com.fr.swift.query.aggregator.extension;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Moira
 * @date 2020/2/4
 * @description
 * @since swift 1.0
 */
public class LimitRowAggregatorTest {

    @Test
    public void testAggregate() {
        LimitRowAggregator aggregator = new LimitRowAggregator(1);
        RowTraversal traversal = AllShowBitMap.of(5);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn);
        EasyMock.expect(dictionaryEncodedColumn.getValueByRow(EasyMock.anyInt())).andReturn("aa");
        control.replay();
        LimitRowAggregatorValue limitRowAggregatorValue;
        limitRowAggregatorValue = aggregator.aggregate(traversal, mockColumn);
        Set<Object> set = new HashSet<>();
        set.add("aa");
        assertEquals(limitRowAggregatorValue.calculateValue(), set);
    }

    @Test
    public void testCombine() {
        LimitRowAggregatorValue value = new LimitRowAggregatorValue();
        LimitRowAggregatorValue other = new LimitRowAggregatorValue();
        Set<Object> set1 = new HashSet<>();
        Set<Object> set2 = new HashSet<>();
        Set<Object> set3 = new HashSet<>();
        set1.add("aa");
        set2.add("bb");
        set2.add("cc");
        set3.addAll(set1);
        set3.addAll(set2);
        value.setValue(set1);
        other.setValue(set2);
        LimitRowAggregator aggregator = new LimitRowAggregator(1);
        aggregator.combine(value, other);
        assertEquals(set3, value.calculateValue());

    }
}