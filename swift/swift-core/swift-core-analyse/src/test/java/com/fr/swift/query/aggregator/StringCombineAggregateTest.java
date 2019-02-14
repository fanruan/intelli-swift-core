package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

/**
 * Created by pony on 2018/3/26.
 */
public class StringCombineAggregateTest extends TestCase {

    public void testAggregate() {
        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dic = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dic).anyTimes();
        EasyMock.expect(dic.getIndexByRow(EasyMock.anyInt())).andReturn(0).anyTimes();
        EasyMock.expect(dic.getValueByRow(EasyMock.anyInt())).andReturn("a").anyTimes();
        control.replay();
        StringAggregateValue value = (StringAggregateValue) StringCombineAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), "a/a/a/a");
    }

    public void testCombine() {
        StringAggregateValue v1 = new StringAggregateValue();
        v1.setValue("a");
        StringAggregateValue v2 = new StringAggregateValue();
        v2.setValue("b");
        StringCombineAggregate.INSTANCE.combine(v1, v2);
        assertEquals(v1.getValue(), "a/b");
        assertEquals(v1.calculate(), 0.0);
        assertEquals(v1.calculateValue(), "a/b");
    }
}