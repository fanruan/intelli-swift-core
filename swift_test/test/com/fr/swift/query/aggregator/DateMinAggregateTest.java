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
public class DateMinAggregateTest extends TestCase {

    public void testAggregate() {
        RowTraversal bitMap = AllShowBitMap.newInstance(2);
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
        LongAmountAggregateValue value = (LongAmountAggregateValue) DateMinAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), Long.valueOf(2));
    }

    public void testCombine() {
        LongAmountAggregateValue v1 = new LongAmountAggregateValue();
        v1.setValue(1);
        LongAmountAggregateValue v2 = new LongAmountAggregateValue();
        v2.setValue(2);
        DateMinAggregate.INSTANCE.combine(v1, v2);
        assertEquals(v1.getValue(), 2);
        assertEquals(v1.calculate(), 2.0);
    }
}