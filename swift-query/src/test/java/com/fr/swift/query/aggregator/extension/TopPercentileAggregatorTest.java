package com.fr.swift.query.aggregator.extension;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by lyon on 2019/1/24.
 */
public class TopPercentileAggregatorTest {

    @Test
    public void aggregate() {
        Aggregator aggregator = new TopPercentileAggregator(90, 3);
        Column column = EasyMock.createMock(Column.class);
        BitmapIndexedColumn indexedColumn = EasyMock.createMock(BitmapIndexedColumn.class);
        EasyMock.expect(indexedColumn.getNullIndex()).andReturn(null);
        EasyMock.expect(column.getBitmapIndex()).andReturn(indexedColumn);
        DetailColumn detailColumn = EasyMock.createMock(DetailColumn.class);
        for (long i = 0; i < 10; i++) {
            EasyMock.expect(detailColumn.getLong((int) i)).andReturn(i * 1000);
        }
        EasyMock.expect(column.getDetailColumn()).andReturn(detailColumn);
        RowTraversal traversal = BitMaps.newRangeBitmap(1, 10);
        EasyMock.replay(column, indexedColumn, detailColumn);
        double value = aggregator.aggregate(traversal, column).calculate();
        assertEquals(9000, value, 1000);
    }
}