package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.BitMapColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

/**
 * Created by pony on 2018/3/27.
 */
public class DateMaxAggregateTest extends TestCase {

    public void testAggregate() {
        RowTraversal bitMap = AllShowBitMap.of(2);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dic = control.createMock(DictionaryEncodedColumn.class);
        DetailColumn detailColumn = control.createMock(DetailColumn.class);
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dic).anyTimes();
        EasyMock.expect(mockColumn.getDetailColumn()).andReturn(detailColumn).anyTimes();
        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(detailColumn.getLong(EasyMock.anyInt())).andReturn(2l).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        control.replay();
        DateAmountAggregateValue value = (DateAmountAggregateValue) DateMaxAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), Long.valueOf(2));
    }

    public void testCombine() {
        DateAmountAggregateValue v1 = new DateAmountAggregateValue();
        v1.setValue(1);
        DateAmountAggregateValue v2 = new DateAmountAggregateValue();
        v2.setValue(2);
        DateMaxAggregate.INSTANCE.combine(v1, v2);
        assertEquals(v1.getValue(), 2);
        assertEquals(v1.calculate(), 2.0);
    }
}
