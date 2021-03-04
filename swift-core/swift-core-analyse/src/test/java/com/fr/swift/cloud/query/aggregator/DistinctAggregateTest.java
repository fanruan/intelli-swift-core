package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.bitmap.impl.AllShowBitMap;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.expect;

public class DistinctAggregateTest extends TestCase {

    public void testAggregate() {

        DistinctCountAggregatorValue an;
        RowTraversal traversal = AllShowBitMap.of(5);
        IMocksControl control = EasyMock.createControl();
        Column column = control.createMock(Column.class);
        DictionaryEncodedColumn dic = control.createMock(DictionaryEncodedColumn.class);

        expect(column.getDictionaryEncodedColumn()).andReturn(dic).anyTimes();
        expect(dic.getValueByRow(0)).andReturn(1 << 18).anyTimes();
        expect(dic.getValueByRow(1)).andReturn(3).anyTimes();
        expect(dic.getValueByRow(2)).andReturn(4).anyTimes();
        expect(dic.getValueByRow(3)).andReturn(7).anyTimes();
        expect(dic.getValueByRow(4)).andReturn(8).anyTimes();

        control.replay();

        double count = 5;
        DistinctAggregate distinctCalculator = (DistinctAggregate) DistinctAggregate.INSTANCE;
        an = distinctCalculator.aggregate(traversal, column);

        assertEquals(true, an.getValues().contains(1 << 18));
        assertEquals(true, an.getValues().contains(3));
        assertEquals(true, an.getValues().contains(4));
        assertEquals(false, an.getValues().contains(5));
        assertEquals(false, an.getValues().contains(6));
        assertEquals(true, an.getValues().contains(7));
        assertEquals(true, an.getValues().contains(8));
        assertEquals(count, an.calculate());

        control.verify();
    }

    public void testCombine() {
        DistinctCountAggregatorValue value = new DistinctCountAggregatorValue();
        DistinctCountAggregatorValue other = new DistinctCountAggregatorValue();
        Set set1 = new HashSet<Object>();
        Set set2 = new HashSet<Object>();

        set1.add(1);
        set1.add(4);
        set1.add(7);
        set1.add(9);

        set2.add(1);
        set2.add(6);
        set2.add(7);
        set2.add(10);

        value.setValues(set1);
        other.setValues(set2);

        double expect = 6.0;
        DistinctAggregate distinctCalculator = (DistinctAggregate) DistinctAggregate.INSTANCE;
        distinctCalculator.combine(value, other);

        assertEquals(true, value.getValues().contains(1));
        assertEquals(false, value.getValues().contains(3));
        assertEquals(true, value.getValues().contains(4));
        assertEquals(false, value.getValues().contains(5));
        assertEquals(true, value.getValues().contains(6));
        assertEquals(true, value.getValues().contains(7));
        assertEquals(false, value.getValues().contains(8));
        assertEquals(true, value.getValues().contains(9));
        assertEquals(true, value.getValues().contains(10));
        assertEquals(expect, value.calculate());

    }
}
