package com.fr.swift.query.aggregator.extension;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Moira
 * @date 2020/2/4
 * @description
 * @since swift 1.0
 */
public class FirstRowAggregatorTest {

    @Test
    public void aggregate() {
        FirstRowAggregator aggregator = FirstRowAggregator.INSTANCE;
        RowTraversal traversal = AllShowBitMap.of(5);
        IMocksControl control = EasyMock.createControl();
        Column mockColumn = control.createMock(Column.class);
        DictionaryEncodedColumn dictionaryEncodedColumn = control.createMock(DictionaryEncodedColumn.class);
        EasyMock.expect(mockColumn.getDictionaryEncodedColumn()).andReturn(dictionaryEncodedColumn);
        EasyMock.expect(dictionaryEncodedColumn.getValueByRow(EasyMock.anyInt())).andReturn("aa");
        control.replay();
        FirstRowAggregatorValue firstRowAggregatorValue;
        firstRowAggregatorValue = aggregator.aggregate(traversal, mockColumn);
        assertEquals(firstRowAggregatorValue.calculateValue(), "aa");
    }
}