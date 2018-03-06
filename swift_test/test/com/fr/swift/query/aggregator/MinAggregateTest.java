package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
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

public class MinAggregateTest extends TestCase {

    private MinAggregate min = MinAggregate.INSTANCE;


    public void testAggregateInt() {
        RowTraversal bitMap = AllShowBitMap.newInstance(5);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        IntDetailColumn mockIntColumn = control.createMock(IntDetailColumn.class);

        try {
            mockColumn.getDetailColumn();
            expectLastCall().andReturn(mockIntColumn);
            expect(mockIntColumn.getInt(0)).andReturn(1);
            expect(mockIntColumn.getInt(1)).andReturn(3);
            expect(mockIntColumn.getInt(2)).andReturn(5);
            expect(mockIntColumn.getInt(3)).andReturn(7);
            expect(mockIntColumn.getInt(4)).andReturn(0);
            control.replay();

            double minNumber = 0;
            DoubleAmountAggregateValue an = min.aggregate(bitMap, mockColumn);
            assertEquals(an.getValue(), minNumber);
            control.verify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testAggregateLongSum() {
        RowTraversal bitMap = AllShowBitMap.newInstance(5);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        LongDetailColumn mockLongColumn = control.createMock(LongDetailColumn.class);

        try {
            mockColumn.getDetailColumn();
            expectLastCall().andReturn(mockLongColumn);
            expect(mockLongColumn.getLong(0)).andReturn(1l);
            expect(mockLongColumn.getLong(1)).andReturn(3l);
            expect(mockLongColumn.getLong(2)).andReturn(5l);
            expect(mockLongColumn.getLong(3)).andReturn(7l);
            expect(mockLongColumn.getLong(4)).andReturn(0l);
            control.replay();

            double minNumber = 0;
            DoubleAmountAggregateValue an = min.aggregate(bitMap, mockColumn);
            assertEquals(an.getValue(), minNumber);
            control.verify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testAggregateDoubleSum() {
        RowTraversal bitMap = AllShowBitMap.newInstance(5);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DoubleDetailColumn mockDoubleColumn = control.createMock(DoubleDetailColumn.class);

        try {
            mockColumn.getDetailColumn();
            expectLastCall().andReturn(mockDoubleColumn);
            expect(mockDoubleColumn.getDouble(0)).andReturn(1.0);
            expect(mockDoubleColumn.getDouble(1)).andReturn(3.3);
            expect(mockDoubleColumn.getDouble(2)).andReturn(5.4);
            expect(mockDoubleColumn.getDouble(3)).andReturn(7.1);
            expect(mockDoubleColumn.getDouble(4)).andReturn(0.2);
            control.replay();

            double minNumber = 0.2;
            DoubleAmountAggregateValue an = min.aggregate(bitMap, mockColumn);
            assertEquals(an.getValue(), minNumber);
            control.verify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCombine() {
        DoubleAmountAggregateValue valueTest1 = new DoubleAmountAggregateValue();
        DoubleAmountAggregateValue otherTest1 = new DoubleAmountAggregateValue();
        DoubleAmountAggregateValue valueTest2 = new DoubleAmountAggregateValue();
        DoubleAmountAggregateValue otherTest2 = new DoubleAmountAggregateValue();

        valueTest1.setValue(-10);
        valueTest2.setValue(10);
        otherTest1.setValue(-2);
        otherTest2.setValue(20);

        MinAggregate min = MinAggregate.INSTANCE;
        min.combine(valueTest1, otherTest1);
        min.combine(valueTest2, otherTest2);

        assertEquals(valueTest1.getValue(), -10.0);
        assertEquals(valueTest2.getValue(), 10.0);
    }
}
