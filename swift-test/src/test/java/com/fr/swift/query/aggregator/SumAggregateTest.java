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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

/**
 * @author liu
 */

public class SumAggregateTest extends TestCase {

    public void testAggregateInt() {

        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
//        IntDetailColumn mockIntColumn = control.createMock(IntDetailColumn.class);
        IntDetailColumn detailColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.INTEGER).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        expect(mockColumn.getDetailColumn()).andReturn(detailColumn).anyTimes();
        control.replay();


        double sum = 17.0;
        SumAggregate sumCalculator = (SumAggregate)SumAggregate.INSTANCE;
        DoubleAmountAggregatorValue an = sumCalculator.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        control.verify();
    }

    public void testAggregateLong() {

        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        LongDetailColumn longDetailColumn = new TempLongDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.LONG).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(longDetailColumn);
        control.replay();

        double sum = 9.0;
        SumAggregate sumCalculator = (SumAggregate)SumAggregate.INSTANCE;
        DoubleAmountAggregatorValue an = sumCalculator.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        control.verify();
    }

    public void testAggregateDouble() {

        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DoubleDetailColumn doubleDetailColumn = new TempDoubleDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.DOUBLE).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        mockColumn.getDetailColumn();
        expectLastCall().andReturn(doubleDetailColumn);

        control.replay();

        double sum = 19.0;
        SumAggregate sumCalculator = (SumAggregate)SumAggregate.INSTANCE;
        DoubleAmountAggregatorValue an = sumCalculator.aggregate(bitMap, mockColumn);
        assertEquals(sum, an.getValue());
        control.verify();
    }

    public void testCombine() {
        DoubleAmountAggregatorValue valueTest1 = new DoubleAmountAggregatorValue();
        DoubleAmountAggregatorValue otherTest1 = new DoubleAmountAggregatorValue();
        DoubleAmountAggregatorValue valueTest2 = new DoubleAmountAggregatorValue();
        DoubleAmountAggregatorValue otherTest2 = new DoubleAmountAggregatorValue();

            valueTest1.setValue(-10.1);
            valueTest2.setValue(10.4);
            otherTest1.setValue(13.3);
            otherTest2.setValue(0);

            SumAggregate sum = (SumAggregate)SumAggregate.INSTANCE;
            sum.combine(valueTest1, otherTest1);
            sum.combine(valueTest2, otherTest2);

            assertEquals(valueTest1.getValue(), -10.1 + 13.3);
            assertEquals(valueTest2.getValue(), 10.4);
    }
}
