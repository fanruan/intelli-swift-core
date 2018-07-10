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
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.expectLastCall;

public class MinAggregateTest extends TestCase {

    private MinAggregate min = (MinAggregate)MinAggregate.INSTANCE;


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

        double minNumber = 1;
        DoubleAmountAggregatorValue an = min.aggregate(bitMap, mockColumn);
        assertEquals(an.getValue(), minNumber);
        control.verify();
    }

    public void testAggregateLongSum() {
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

        double minNumber = 0;
        DoubleAmountAggregatorValue an = min.aggregate(bitMap, mockColumn);
        assertEquals(an.getValue(), minNumber);
        control.verify();
    }

    public void testAggregateDoubleSum() {
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

        double minNumber = 1.2;
        DoubleAmountAggregatorValue an = min.aggregate(bitMap, mockColumn);
        assertEquals(an.getValue(), minNumber);
        control.verify();
    }

    public void testCombine() {
        DoubleAmountAggregatorValue valueTest1 = new DoubleAmountAggregatorValue();
        DoubleAmountAggregatorValue otherTest1 = new DoubleAmountAggregatorValue();
        DoubleAmountAggregatorValue valueTest2 = new DoubleAmountAggregatorValue();
        DoubleAmountAggregatorValue otherTest2 = new DoubleAmountAggregatorValue();

        valueTest1.setValue(-10);
        valueTest2.setValue(10);
        otherTest1.setValue(-2);
        otherTest2.setValue(20);

        MinAggregate min = (MinAggregate)MinAggregate.INSTANCE;
        min.combine(valueTest1, otherTest1);
        min.combine(valueTest2, otherTest2);

        assertEquals(valueTest1.getValue(), -10.0);
        assertEquals(valueTest2.getValue(), 10.0);
    }
}
