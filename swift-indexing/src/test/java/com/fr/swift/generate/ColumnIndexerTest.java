package com.fr.swift.generate;

import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn.Putter;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.intlist.BaseIntListExternalMap;
import com.fr.swift.structure.external.map.intlist.IntListExternalMapFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/1/9
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({PerformancePlugManager.class, IntListExternalMapFactory.class, ColumnIndexer.class})
public class ColumnIndexerTest {

    @Mock
    Segment seg;
    @Mock
    private Column<Long> column;
    @Mock
    private DetailColumn<Long> detailColumn;
    @Mock
    private BitmapIndexedColumn bitmapColumn;
    @Mock
    private DictionaryEncodedColumn<Long> dictColumn;

    @Before
    public void setUp() throws Exception {
        SwiftMetaData meta = mock(SwiftMetaData.class);
        // seg
        when(seg.getMetaData()).thenReturn(meta);
        when(seg.getRowCount()).thenReturn(4);
        when(seg.<Long>getColumn(new ColumnKey("column"))).thenReturn(column);

        IResourceLocation location = mock(IResourceLocation.class);
        // column
        when(column.getLocation()).thenReturn(location);
        when(column.getDetailColumn()).thenReturn(detailColumn);
        when(column.getBitmapIndex()).thenReturn(bitmapColumn);
        when(column.getDictionaryEncodedColumn()).thenReturn(dictColumn);

        // base column
        when(detailColumn.get(0)).thenReturn(1L);
        when(detailColumn.get(1)).thenReturn(Long.MIN_VALUE);
        when(detailColumn.get(2)).thenReturn(3L);
        when(detailColumn.get(3)).thenReturn(1L);
        when(dictColumn.putter()).thenReturn(mock(Putter.class));
        when(dictColumn.getComparator()).thenReturn(Comparators.<Long>asc());
        when(bitmapColumn.getNullIndex()).thenReturn(new RangeBitmap(1, 2));

        // location
        when(location.getStoreType()).thenReturn(StoreType.FINE_IO);
        when(location.getAbsolutePath()).thenReturn("/cubes/table/0/seg0/column");
        when(location.buildChildLocation(anyString())).thenReturn(location);

        // meta
        SwiftMetaDataColumn columnMeta = mock(SwiftMetaDataColumn.class);
        when(meta.getColumn("column")).thenReturn(columnMeta);
        when(columnMeta.getType()).thenReturn(Types.BIGINT);

        // static
        mockStatic(PerformancePlugManager.class, IntListExternalMapFactory.class);
        PerformancePlugManager performancePlugManager = mock(PerformancePlugManager.class);
        when(PerformancePlugManager.getInstance()).thenReturn(performancePlugManager);
        when(performancePlugManager.isDiskSort()).thenReturn(true);
    }

    @Test
    public void buildIndexViaExternalMap() throws Exception {
        // external map
        BaseIntListExternalMap<Long> externalMap = mock(BaseIntListExternalMap.class);
        when(IntListExternalMapFactory.getIntListExternalMap(ArgumentMatchers.<ClassType>any(), ArgumentMatchers.<Comparator<Long>>any(), anyString(), anyBoolean()))
                .thenReturn(externalMap);
        IntList indexOf1 = IntListFactory.createIntList();
        indexOf1.add(0);
        indexOf1.add(3);
        IntList indexOf3 = IntListFactory.createIntList();
        indexOf3.add(2);
        when(externalMap.iterator()).thenReturn(Arrays.<Entry<Long, IntList>>asList(
                Pair.of(1L, indexOf1),
                Pair.of(3L, indexOf3)).iterator());

        new ColumnIndexer<Object>(new ColumnKey("column"), Collections.singletonList(seg)).buildIndex();

        // 读三行非空明细
        verify(detailColumn).get(0);
        verify(detailColumn).get(2);
        verify(detailColumn).get(3);
        // put三行
        verify(externalMap).put(1L, 0);
        verify(externalMap).put(3L, 2);
        verify(externalMap).put(1L, 3);

        // dict put 3个非空value，对所有value包括空值put index，size包括空值
        verify(dictColumn.putter()).putValue(0, null);
        verify(dictColumn.putter()).putValue(1, 1L);
        verify(dictColumn.putter()).putValue(2, 3L);

        verify(dictColumn.putter()).putIndex(0, 1);
        verify(dictColumn.putter()).putIndex(1, 0);
        verify(dictColumn.putter()).putIndex(2, 2);
        verify(dictColumn.putter()).putIndex(3, 1);

        verify(dictColumn.putter()).putSize(3);
    }

    @Test
    public void buildIndexViaTreeMap() throws Exception {
        PerformancePlugManager performancePlugManager = PerformancePlugManager.getInstance();
        when(performancePlugManager.isDiskSort()).thenReturn(false);

        TreeMap<Long, IntList> map = spy(new TreeMap<Long, IntList>());
        whenNew(TreeMap.class).withAnyArguments().thenReturn(map);

        new ColumnIndexer<Object>(new ColumnKey("column"), Collections.singletonList(seg)).buildIndex();

        // 读三行非空明细
        verify(detailColumn).get(0);
        verify(detailColumn).get(2);
        verify(detailColumn).get(3);
        // put两个非空值的行号list
        verify(map).put(eq(1L), ArgumentMatchers.<IntList>any());
        verify(map).put(eq(3L), ArgumentMatchers.<IntList>any());

        // dict put 3个非空value，对所有value包括空值put index，size包括空值
        verify(dictColumn.putter()).putValue(0, null);
        verify(dictColumn.putter()).putValue(1, 1L);
        verify(dictColumn.putter()).putValue(2, 3L);

        verify(dictColumn.putter()).putIndex(0, 1);
        verify(dictColumn.putter()).putIndex(1, 0);
        verify(dictColumn.putter()).putIndex(2, 2);
        verify(dictColumn.putter()).putIndex(3, 1);

        verify(dictColumn.putter()).putSize(3);
    }
}