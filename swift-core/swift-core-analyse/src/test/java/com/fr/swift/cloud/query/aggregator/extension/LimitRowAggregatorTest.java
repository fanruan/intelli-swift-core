package com.fr.swift.cloud.query.aggregator.extension;

import com.fr.swift.cloud.bitmap.impl.AllShowBitMap;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        List<Object> list = new ArrayList<>();
        list.add("aa");
        assertEquals(limitRowAggregatorValue.calculateValue(), list);
    }

    @Test
    public void testCombine() {
        LimitRowAggregatorValue value = new LimitRowAggregatorValue();
        LimitRowAggregatorValue other = new LimitRowAggregatorValue();
        List<Object> list1 = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        List<Object> list3 = new ArrayList<>();
        list1.add("aa");
        list2.add("bb");
        list2.add("cc");
        list3.addAll(list1);
        list3.addAll(list2);
        value.setValue(list1);
        other.setValue(list2);
        LimitRowAggregator aggregator = new LimitRowAggregator(1);
        aggregator.combine(value, other);
        assertEquals(list3, value.calculateValue());

    }
}