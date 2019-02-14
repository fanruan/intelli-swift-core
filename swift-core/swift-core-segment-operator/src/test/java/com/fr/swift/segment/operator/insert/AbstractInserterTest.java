package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.IdBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/15
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class AbstractInserterTest {

    @Mock
    private Segment seg;
    @Mock
    private Column<Object> column;

    @Before
    public void setUp() throws Exception {
        SwiftMetaData meta = mock(SwiftMetaData.class);
        when(meta.getFieldNames()).thenReturn(Collections.singletonList("c1"));

        when(column.getDetailColumn()).thenReturn(mock(DetailColumn.class));
        when(column.getBitmapIndex()).thenReturn(mock(BitmapIndexedColumn.class));

        when(seg.getMetaData()).thenReturn(meta);
        when(seg.isReadable()).thenReturn(true);
        when(seg.getColumn(new ColumnKey("c1"))).thenReturn(column);
    }

    @Test
    public void putRow() {
        Row row = mock(Row.class);
        when(row.getValue(0)).thenReturn(null);
        Row row1 = mock(Row.class);
        when(row1.getValue(0)).thenReturn(1);

        Ins ins = new Ins(seg, Collections.singletonList("c1"));
        ins.putRow(0, row);
        ins.putRow(1, row1);

        verify(column.getDetailColumn()).put(0, null);
        verify(column.getDetailColumn()).put(1, 1);
        verifyNoMoreInteractions(column.getDetailColumn());
    }

    @Test
    public void putNullIndex() {
        Row row = mock(Row.class);
        when(row.getValue(0)).thenReturn(null);
        Row row1 = mock(Row.class);
        when(row1.getValue(0)).thenReturn(1);

        when(seg.isReadable()).thenReturn(false);
        BitmapIndexedColumn bitmapIndexedColumn = column.getBitmapIndex();

        Ins ins = new Ins(seg, Collections.singletonList("c1"));
        ins.putRow(0, row);
        ins.putRow(1, row1);
        ins.putNullIndex();

        verify(bitmapIndexedColumn).putNullIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 1 && item.contains(0);
            }
        }));

        when(seg.isReadable()).thenReturn(true);
        when(bitmapIndexedColumn.getNullIndex()).thenReturn(IdBitMap.of(2));

        ins = new Ins(seg, Collections.singletonList("c1"));
        ins.putRow(0, row);
        ins.putRow(1, row1);
        ins.putNullIndex();

        verify(bitmapIndexedColumn).putNullIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 2 && item.contains(0) && item.contains(2);
            }
        }));

        // 读出错的情况
        when(seg.isReadable()).thenReturn(true);
        when(bitmapIndexedColumn.getNullIndex()).thenThrow(Exception.class);

        ins = new Ins(seg, Collections.singletonList("c1"));
        ins.putRow(0, row);
        ins.putRow(1, row1);
        ins.putNullIndex();

        verify(bitmapIndexedColumn, times(2)).putNullIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 1 && item.contains(0);
            }
        }));
    }

    @Test
    public void putSegmentInfo() {
        when(seg.getAllShowIndex()).thenReturn(new RangeBitmap(0, 1));

        Ins ins = new Ins(seg, Collections.singletonList("c1"));
        ins.putSegmentInfo(1, 2);

        verify(seg).putRowCount(2);
        verify(seg).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 2 && item.contains(0) && item.contains(1);
            }
        }));

        when(seg.isReadable()).thenReturn(false);

        ins.putSegmentInfo(1, 2);

        verify(seg, times(2)).putRowCount(2);
        verify(seg, times(2)).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 2 && item.contains(0) && item.contains(1);
            }
        }));

        when(seg.getAllShowIndex()).thenThrow(Exception.class);

        ins.putSegmentInfo(1, 2);

        verify(seg, times(3)).putRowCount(2);
        verify(seg, times(3)).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap item) {
                return item.getCardinality() == 2 && item.contains(0) && item.contains(1);
            }
        }));
    }

    @Test
    public void release() {
        IResourceLocation location = mock(IResourceLocation.class);
        when(location.getStoreType()).thenReturn(StoreType.FINE_IO);
        when(column.getLocation()).thenReturn(location);

        when(seg.isHistory()).thenReturn(true);

        new Ins(seg, Collections.singletonList("c1")).release();

        verify(column.getDetailColumn()).release();
        verify(column.getBitmapIndex()).release();
        verify(seg).release();
    }

    @Test
    public void getFields() {
        List<String> fields = Collections.singletonList("c1");

        assertEquals(fields, new Ins(seg, fields).getFields());
    }

    class Ins extends BaseInserter {
        Ins(Segment segment) {
            super(segment);
        }

        public Ins(Segment segment, List<String> fields) {
            super(segment, fields);
        }
    }
}