package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
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

        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockIntColumn);
//        expect(mockIntColumn.getInt(0)).andReturn(1);
//        expect(mockIntColumn.getInt(1)).andReturn(3);
        control.replay();

        double sum = 17.0;
        AverageAggregate avg = AverageAggregate.INSTANCE;
        DoubleAverageAggregateValue an = avg.aggregate(bitMap, mockColumn);
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

        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockLongColumn);
//        expect(mockLongColumn.getLong(0)).andReturn(1l);
//        expect(mockLongColumn.getLong(1)).andReturn(3l);
//        expect(mockLongColumn.getLong(2)).andReturn(11l);
        control.replay();

        double sum = 9.0;
        AverageAggregate avg = AverageAggregate.INSTANCE;
        DoubleAverageAggregateValue an = avg.aggregate(bitMap, mockColumn);
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

        mockColumn.getDetailColumn();
        expectLastCall().andReturn(mockDoubleColumn);
//        expect(mockDoubleColumn.getDouble(0)).andReturn(1.0);
//        expect(mockDoubleColumn.getDouble(1)).andReturn(3.0);
//        expect(mockDoubleColumn.getDouble(2)).andReturn(1.0);
//        expect(mockDoubleColumn.getDouble(3)).andReturn(7.0);
        control.replay();

        double sum = 19.0;
        AverageAggregate avg = AverageAggregate.INSTANCE;
        DoubleAverageAggregateValue an = avg.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        assertEquals(4, an.getRowCount());
        assertEquals(sum/4, an.calculate());
        control.verify();


    }


    public void testCombine() {
        DoubleAverageAggregateValue valueTest1 = new DoubleAverageAggregateValue();
        DoubleAverageAggregateValue otherTest1 = new DoubleAverageAggregateValue();
        DoubleAverageAggregateValue valueTest2 = new DoubleAverageAggregateValue();
        DoubleAverageAggregateValue otherTest2 = new DoubleAverageAggregateValue();

        valueTest1.setValue(-10.1);
        valueTest1.setRowCount(10);
        valueTest2.setValue(10.4);
        valueTest2.setRowCount(2);
        otherTest1.setValue(13.3);
        otherTest1.setRowCount(12);
        otherTest2.setValue(0);
        otherTest2.setRowCount(2);

        AverageAggregate avg = AverageAggregate.INSTANCE;
        avg.combine(valueTest1, otherTest1);
        avg.combine(valueTest2, otherTest2);

        assertEquals(3.200000000000001, valueTest1.getValue());
        assertEquals(22, valueTest1.getRowCount());
        assertEquals(10.4, valueTest2.getValue());
        assertEquals(4, valueTest2.getRowCount());
    }

}
