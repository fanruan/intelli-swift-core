package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.BitMapColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.expect;

public class MedianAggregateTest extends TestCase {

    public void testAggregateInt() {

        RowTraversal bitMap = AllShowBitMap.newInstance(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dic = control.createMock(DictionaryEncodedColumn.class);
        IntDetailColumn detailColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dic).anyTimes();
        EasyMock.expect(dic.size()).andReturn(4).anyTimes();
        EasyMock.expect(dic.getIndexByRow(EasyMock.anyInt())).andReturn(3).anyTimes();
        EasyMock.expect(dic.getValue(EasyMock.anyInt())).andReturn(2.0).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        expect(mockColumn.getDetailColumn()).andReturn(detailColumn).anyTimes();
        control.replay();

        MedianAggregatorValue value = (MedianAggregatorValue) MedianAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), 2.0);
    }

    public void testCombine() {
        //
    }
}
