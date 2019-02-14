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

import java.util.TreeMap;

import static org.easymock.EasyMock.expect;

public class MedianAggregateTest extends TestCase {

    public void testAggregateOdd() {
        //奇数个数据
        RowTraversal bitMap = AllShowBitMap.of(3);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dic = control.createMock(DictionaryEncodedColumn.class);
        IntDetailColumn detailColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
//        IntDetailColumn detailColumn = control.createMock(IntDetailColumn.class);
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dic).anyTimes();
        EasyMock.expect(dic.size()).andReturn(3).anyTimes();
        EasyMock.expect(dic.getIndexByRow(EasyMock.anyInt())).andReturn(2).times(1);
        EasyMock.expect(dic.getIndexByRow(EasyMock.anyInt())).andReturn(1).times(2);
        EasyMock.expect(dic.getValue(EasyMock.anyInt())).andReturn(2.0).times(1);
        EasyMock.expect(dic.getValue(EasyMock.anyInt())).andReturn(1.0).times(2);
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        expect(mockColumn.getDetailColumn()).andReturn(detailColumn).anyTimes();
        control.replay();

        MedianAggregatorValue value = (MedianAggregatorValue) MedianAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), 1.0);
        assertEquals(value.getCount(), 3);
        assertEquals(value.getMedian(), 1.0);
    }

    public void testAggregateEven() {
        //偶数个数据
        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dic = control.createMock(DictionaryEncodedColumn.class);
        IntDetailColumn detailColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dic).anyTimes();
        EasyMock.expect(dic.size()).andReturn(4).anyTimes();
        EasyMock.expect(dic.getIndexByRow(EasyMock.anyInt())).andReturn(2).times(2);
        EasyMock.expect(dic.getIndexByRow(EasyMock.anyInt())).andReturn(1).times(2);
        EasyMock.expect(dic.getValue(EasyMock.anyInt())).andReturn(2.0).times(2);
        EasyMock.expect(dic.getValue(EasyMock.anyInt())).andReturn(1.0).times(2);
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        expect(mockColumn.getDetailColumn()).andReturn(detailColumn).anyTimes();
        control.replay();

        MedianAggregatorValue value = (MedianAggregatorValue) MedianAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), 1.5);
        assertEquals(value.getCount(), 4);
        assertEquals(value.getMedian(), 1.5);
    }


    public void testCombine() {
        TreeMap<Double, Integer> value = new TreeMap<Double, Integer>();
        TreeMap<Double, Integer> other = new TreeMap<Double, Integer>();
        value.put(12.0, 3);
        value.put(2.0, 2);
        other.put(3.0, 4);
        other.put(1.0, 2);
        MedianAggregatorValue v1 = new MedianAggregatorValue();
        MedianAggregatorValue o1 = new MedianAggregatorValue();
        v1.setCount(5);
        v1.setValues(value);
        v1.setMedian(12.0);
        o1.setCount(6);
        o1.setValues(other);
        o1.setMedian(3.0);
        MedianAggregate.INSTANCE.combine(v1, o1);

        assertEquals(v1.getCount(), 11);
        assertEquals(v1.getMedian(), 3.0);
        assertEquals(v1.calculateValue(), 3.0);
        value.putAll(other);
        assertEquals(v1.getValues(), value);
    }
}
