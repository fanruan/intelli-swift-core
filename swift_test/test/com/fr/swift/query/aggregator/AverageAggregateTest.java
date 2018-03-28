package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.base.BitMapColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

public class AverageAggregateTest extends TestCase {

    public void testAggregateInt() {
        RowTraversal bitMap = AllShowBitMap.newInstance(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        IntDetailColumn mockIntColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockIntColumn);
        control.replay();

        double sum = 17.0;
        AverageAggregate avg = (AverageAggregate)AverageAggregate.INSTANCE;
        DoubleAverageAggregatorValue an = avg.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        assertEquals(4, an.getRowCount());
        assertEquals(sum/4, an.calculate());
        control.verify();


    }

    public void testAggregateLong() {
        RowTraversal bitMap = AllShowBitMap.newInstance(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        LongDetailColumn mockLongColumn = new TempLongDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockLongColumn);
        control.replay();

        double sum = 9.0;
        AverageAggregate avg = (AverageAggregate)AverageAggregate.INSTANCE;
        DoubleAverageAggregatorValue an = avg.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        assertEquals(4, an.getRowCount());
        assertEquals(sum/4, an.calculate());
        control.verify();


    }

    public void testAggregateDouble() {
        RowTraversal bitMap = AllShowBitMap.newInstance(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DoubleDetailColumn mockDoubleColumn = new TempDoubleDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockDoubleColumn);
        control.replay();

        double sum = 19.0;
        AverageAggregate avg = (AverageAggregate)AverageAggregate.INSTANCE;
        DoubleAverageAggregatorValue an = avg.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        assertEquals(4, an.getRowCount());
        assertEquals(sum/4, an.calculate());
        control.verify();


    }


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

        AverageAggregate avg = (AverageAggregate)AverageAggregate.INSTANCE;
        avg.combine(valueTest1, otherTest1);
        avg.combine(valueTest2, otherTest2);

        assertEquals(3.200000000000001, valueTest1.getValue());
        assertEquals(22, valueTest1.getRowCount());
        assertEquals(10.4, valueTest2.getValue());
        assertEquals(4, valueTest2.getRowCount());
    }

}
