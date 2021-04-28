package com.fr.swift.cloud.query.builder;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Create by lifan on 2019-07-04 16:09
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricFilterAggregator.class})
public class MetricFilterAggregatorTest {


    @Test
    public void aggregate() {
        RowTraversal traversal = mock(RowTraversal.class);
        Column column = mock(Column.class);
        ImmutableBitMap bitMap = mock(ImmutableBitMap.class);
        Aggregator aggregator = mock(Aggregator.class);
        DetailFilter detailFilter = mock(DetailFilter.class);

        //bitmap = null
        AggregatorValue aggregatorValue = mock(AggregatorValue.class);
        when(aggregator.aggregate(traversal, column)).thenReturn(aggregatorValue);
        Assert.assertEquals(aggregatorValue, new MetricFilterAggregator(aggregator, detailFilter).aggregate(traversal, column));

        //bitmap!=null
        //mock traversal.toBitMap()
        ImmutableBitMap mapIndex = mock(ImmutableBitMap.class);
        when(traversal.toBitMap()).thenReturn(mapIndex);

        ImmutableBitMap map = mock(ImmutableBitMap.class);
        when(bitMap.getAnd(mapIndex)).thenReturn(map);
        when(aggregator.aggregate(traversal, column)).thenReturn(aggregatorValue);
        Assert.assertEquals(aggregatorValue, new MetricFilterAggregator(aggregator, detailFilter).aggregate(traversal, column));
    }
}