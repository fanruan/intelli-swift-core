package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.BitMapColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.expect;

public class StandardAggregateTest extends TestCase {
    public void testAggregateInt() {

        RowTraversal bitMap = AllShowBitMap.of(4);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        IntDetailColumn detailColumn = new TempIntDetailColumn(new ResourceLocation("liu"));
        BitMapColumn bitMapColumn = control.createMock(BitMapColumn.class);

        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(dictionaryEncodedColumn.getType()).andReturn(ColumnTypeConstants.ClassType.INTEGER).anyTimes();
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn).anyTimes();

        EasyMock.expect(mockColumn.getBitmapIndex()).andReturn(bitMapColumn).anyTimes();
        EasyMock.expect(bitMapColumn.getNullIndex()).andReturn(null).anyTimes();
        expect(mockColumn.getDetailColumn()).andReturn(detailColumn).anyTimes();
        control.replay();

        StandardAggregatorValue value = (StandardAggregatorValue) StandarDeviationAggregate.INSTANCE.aggregate(bitMap, mockColumn);
        assertEquals(value.calculateValue(), Math.sqrt(48.75 / 4));
    }

    public void testCombine() {
        VarianceAggregatorValue v1 = new VarianceAggregatorValue();
        VarianceAggregatorValue v2 = new VarianceAggregatorValue();
        StandardAggregatorValue s1 = new StandardAggregatorValue();
        StandardAggregatorValue s2 = new StandardAggregatorValue();
        v1.setSum(10);
        v1.setSquareSum(30);
        v1.setCount(4);
        v1.setVariance(5);
        s1.setVariance(v1);
        v2.setSum(6);
        v2.setSquareSum(14);
        v2.setCount(3);
        v2.setVariance(2);
        s2.setVariance(v2);
        StandarDeviationAggregate.INSTANCE.combine(s1, s2);

        assertEquals(s1.calculateValue(), Math.sqrt(52.0 / 49));

    }


}
