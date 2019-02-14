package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.BitMapColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.iterator.RowTraversal;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;

import static org.easymock.EasyMock.expectLastCall;

public class AverageAggregateTest {

    @Test
    public void testAggregateInt() {
        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        IntDetailColumn mockIntColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.INTEGER).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockIntColumn);
        control.replay();

        double sum = 17.0;
        AverageAggregate avg = (AverageAggregate) AverageAggregate.INSTANCE;
        DoubleAverageAggregatorValue an = avg.aggregate(bitMap, mockColumn);
        Assert.assertEquals(sum, an.getValue(), 0);
        Assert.assertEquals(4, an.getRowCount(), 0);
        Assert.assertEquals(sum / 4, an.calculate(), 0);
        control.verify();


    }

    @Test
    public void testAggregateLong() {
        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        LongDetailColumn mockLongColumn = new TempLongDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.LONG).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockLongColumn);
        control.replay();

        double sum = 9.0;
        AverageAggregate avg = (AverageAggregate) AverageAggregate.INSTANCE;
        DoubleAverageAggregatorValue an = avg.aggregate(bitMap, mockColumn);
        Assert.assertEquals(sum, an.getValue(), 0);
        Assert.assertEquals(4, an.getRowCount());
        Assert.assertEquals(sum / 4, an.calculate(), 0);
        control.verify();


    }

    @Test
    public void testAggregateDouble() {
        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DoubleDetailColumn mockDoubleColumn = new TempDoubleDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.DOUBLE).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockDoubleColumn);
        control.replay();

        double sum = 19.0;
        AverageAggregate avg = (AverageAggregate) AverageAggregate.INSTANCE;
        DoubleAverageAggregatorValue an = avg.aggregate(bitMap, mockColumn);
        Assert.assertEquals(sum, an.getValue(), 0);
        Assert.assertEquals(4, an.getRowCount());
        Assert.assertEquals(sum / 4, an.calculate(), 0);
        control.verify();


    }


    @Test
    public void testCombine() {
        DoubleAverageAggregatorValue valueTest1 = new DoubleAverageAggregatorValue();
        DoubleAverageAggregatorValue otherTest1 = new DoubleAverageAggregatorValue();
        DoubleAverageAggregatorValue valueTest2 = new DoubleAverageAggregatorValue();
        DoubleAverageAggregatorValue otherTest2 = new DoubleAverageAggregatorValue();

        valueTest1.setValue(-10.1);
        valueTest1.setRowCount(10);
        valueTest2.setValue(10.4);
        valueTest2.setRowCount(2);
        otherTest1.setValue(13.3);
        otherTest1.setRowCount(12);
        otherTest2.setValue(0);
        otherTest2.setRowCount(2);

        AverageAggregate avg = (AverageAggregate) AverageAggregate.INSTANCE;
        avg.combine(valueTest1, otherTest1);
        avg.combine(valueTest2, otherTest2);

        Assert.assertEquals(3.200000000000001, valueTest1.getValue(), 0);
        Assert.assertEquals(22, valueTest1.getRowCount());
        Assert.assertEquals(10.4, valueTest2.getValue(), 0);
        Assert.assertEquals(4, valueTest2.getRowCount(), 0);
    }

}
