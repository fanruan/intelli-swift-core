package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/8
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SegmentUtils.class})
public class SwiftInserterTest {

    @Mock
    private Segment segment;
    @Mock
    private SwiftMetaData meta;

    @Before
    public void setUp() throws Exception {
        when(segment.getMetaData()).thenReturn(meta);
        when(meta.getFieldNames()).thenReturn(Collections.singletonList("c1"));
        Column c1 = mock(Column.class);
        when(segment.getColumn(new ColumnKey("c1"))).thenReturn(c1);

        IResourceLocation location = mock(IResourceLocation.class);
        when(c1.getLocation()).thenReturn(location);

        when(c1.getDetailColumn()).thenReturn(mock(DetailColumn.class));
        when(c1.getBitmapIndex()).thenReturn(mock(BitmapIndexedColumn.class));

        mockStatic(SegmentUtils.class);
    }

    @Test
    public void insertData() throws Exception {
        SwiftResultSet resultSet = mock(SwiftResultSet.class);
        when(resultSet.hasNext()).thenReturn(true, true, true, false);
        when(resultSet.getNextRow()).thenReturn(new ListBasedRow(1),
                new ListBasedRow(new Object[]{null}),
                new ListBasedRow(7));
        when(resultSet.getMetaData()).thenReturn(meta);

        new SwiftInserter(segment).insertData(resultSet);

        Column<Object> c1 = segment.getColumn(new ColumnKey("c1"));
        DetailColumn<Object> detailColumn = c1.getDetailColumn();
        BitmapIndexedColumn bitmapIndex = c1.getBitmapIndex();

        // insert
        verify(detailColumn).put(0, 1);
        verify(detailColumn).put(1, null);
        verify(detailColumn).put(2, 7);

        // close result set
        verify(resultSet).close();

        // put null index, all show index, row count
        verify(bitmapIndex).putNullIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 1 && item.contains(1);
            }
        }));
        verify(segment).putRowCount(3);
        verify(segment).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 3;
            }
        }));

        // release all
        verifyStatic(SegmentUtils.class);
        SegmentUtils.releaseColumns(ArgumentMatchers.<List<Column<Object>>>any());
        verifyStatic(SegmentUtils.class);
        SegmentUtils.release(segment);
    }
}