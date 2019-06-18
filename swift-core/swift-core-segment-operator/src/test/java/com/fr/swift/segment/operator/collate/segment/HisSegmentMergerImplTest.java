package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by lyon on 2019/2/22.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SwiftDatabase.class, SegmentUtils.class})
public class HisSegmentMergerImplTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class, SegmentUtils.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        // mock segment service
        SwiftSegmentService swiftSegmentService = mock(SwiftSegmentService.class);
        when(beanFactory.getBean("segmentServiceProvider", SwiftSegmentService.class)).thenReturn(swiftSegmentService);
        when(swiftSegmentService.tryAppendSegment(ArgumentMatchers.<SourceKey>any(), ArgumentMatchers.<Types.StoreType>any())).thenAnswer(new Answer<SegmentKey>() {
            int order = 0;

            @Override
            public SegmentKey answer(InvocationOnMock invocation) throws Throwable {
                SourceKey tableKey = invocation.getArgument(0);
                Types.StoreType storeType = invocation.getArgument(1);
                return new SwiftSegmentEntity(tableKey, order++, storeType, SwiftSchema.CUBE);
            }
        });

        // mock cube path service
        SwiftCubePathService service = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(service);
        when(service.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void merge() throws Exception {
        SwiftSourceAlloter alloter = mock(SwiftSourceAlloter.class);
        when(alloter.getAllotRule()).thenReturn(new LineAllotRule(6));

        DataSource dataSource = mock(DataSource.class);
        SourceKey tableKey = new SourceKey("tbl");
        when(dataSource.getSourceKey()).thenReturn(tableKey);
        SwiftMetaData metaData = mock(SwiftMetaData.class);
        when(dataSource.getMetadata()).thenReturn(metaData);
        when(metaData.getSwiftSchema()).thenReturn(SwiftSchema.CUBE);
        when(metaData.getFieldNames()).thenReturn(Collections.singletonList("a"));

        Segment testSeg0 = mock(Segment.class);
        when(testSeg0.getRowCount()).thenReturn(6);
        when(testSeg0.getAllShowIndex()).thenReturn(of(0, 1, 2, 3, 4, 5));
        Column column0 = mock(Column.class);
        when(testSeg0.getColumn(new ColumnKey("a"))).thenReturn(column0);
        mockColumn(column0);

        // mock new segment
        final Segment seg0 = mock(Segment.class);
        when(SegmentUtils.newSegment(ArgumentMatchers.<IResourceLocation>any(), ArgumentMatchers.<SwiftMetaData>any())).thenAnswer(new Answer<Segment>() {
            @Override
            public Segment answer(InvocationOnMock invocationOnMock) throws Throwable {
                return seg0;
            }
        });
        mockExpectedColumn(seg0);
        HisSegmentMerger merger = new HisSegmentMergerImpl();
        List<SegmentKey> keys = merger.merge(dataSource, Collections.singletonList(testSeg0), alloter);
        assertEquals(1, keys.size());
        check0(seg0);

        // with deleted rows
        Segment testSeg1 = mock(Segment.class);
        when(testSeg1.getRowCount()).thenReturn(6);
        when(testSeg1.getAllShowIndex()).thenReturn(of(1, 3, 5));
        when(testSeg1.getColumn(new ColumnKey("a"))).thenReturn(column0);
        final Segment seg1 = mock(Segment.class);
        when(SegmentUtils.newSegment(ArgumentMatchers.<IResourceLocation>any(), ArgumentMatchers.<SwiftMetaData>any())).thenAnswer(new Answer<Segment>() {
            @Override
            public Segment answer(InvocationOnMock invocationOnMock) throws Throwable {
                return seg1;
            }
        });
        mockExpectedColumn(seg1);
        keys = merger.merge(dataSource, Collections.singletonList(testSeg1), alloter);
        assertEquals(1, keys.size());
        check1(seg1);

        // 2 sub segments
        Segment testSeg2 = mock(Segment.class);
        when(testSeg2.getRowCount()).thenReturn(6);
        when(testSeg2.getAllShowIndex()).thenReturn(of(0, 2, 3, 5));
        when(testSeg2.getColumn(new ColumnKey("a"))).thenReturn(column0);
        Segment testSeg3 = mock(Segment.class);
        when(testSeg3.getRowCount()).thenReturn(6);
        when(testSeg3.getAllShowIndex()).thenReturn(of(1, 2, 4, 5));
        when(testSeg3.getColumn(new ColumnKey("a"))).thenReturn(column0);
        final Segment seg2 = mock(Segment.class);
        final Segment seg3 = mock(Segment.class);
        when(SegmentUtils.newSegment(ArgumentMatchers.<IResourceLocation>any(), ArgumentMatchers.<SwiftMetaData>any())).thenReturn(seg2, seg3);
        mockExpectedColumn(seg2);
        mockExpectedColumn(seg3);
        keys = merger.merge(dataSource, Arrays.asList(testSeg2, testSeg3), alloter);
        assertEquals(2, keys.size());
        check2(seg2);
        check3(seg3);
    }

    private void check3(Segment segment) {
        verify(segment).putRowCount(2);
        verify(segment).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.newRangeBitmap(0, 2);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        Column column = segment.getColumn(new ColumnKey("a"));
        DetailColumn detail = column.getDetailColumn();
        DictionaryEncodedColumn dict = column.getDictionaryEncodedColumn();
        final BitmapIndexedColumn indexed = column.getBitmapIndex();

        verify(detail).put(0, 5);
        verify(detail).put(1, 3);

        DictionaryEncodedColumn.Putter putter = dict.putter();
        verify(putter).putSize(3);
        verify(putter).putIndex(0, 2);
        verify(putter).putIndex(1, 1);
        verify(putter).putValue(0, null);
        verify(putter).putValue(1, 3);
        verify(putter).putValue(2, 5);

        verify(indexed).putBitMapIndex(eq(0), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.EMPTY_IMMUTABLE;
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(1), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(1);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(2), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(0);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
    }

    private void check2(Segment segment) {
        verify(segment).putRowCount(6);
        verify(segment).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.newRangeBitmap(0, 6);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        Column column = segment.getColumn(new ColumnKey("a"));
        DetailColumn detail = column.getDetailColumn();
        DictionaryEncodedColumn dict = column.getDictionaryEncodedColumn();
        final BitmapIndexedColumn indexed = column.getBitmapIndex();

        verify(detail).put(0, 1);
        verify(detail).put(1, 3);
        verify(detail).put(2, 1);
        verify(detail).put(3, 3);
        verify(detail).put(4, 2);
        verify(detail).put(5, 3);

        DictionaryEncodedColumn.Putter putter = dict.putter();
        verify(putter).putSize(4);
        verify(putter).putIndex(0, 1);
        verify(putter).putIndex(1, 3);
        verify(putter).putIndex(2, 1);
        verify(putter).putIndex(3, 3);
        verify(putter).putIndex(4, 2);
        verify(putter).putIndex(5, 3);
        verify(putter).putValue(0, null);
        verify(putter).putValue(1, 1);
        verify(putter).putValue(2, 2);
        verify(putter).putValue(3, 3);

        verify(indexed).putBitMapIndex(eq(0), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.EMPTY_IMMUTABLE;
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(1), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(0, 2);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(2), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(4);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(3), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(1, 3, 5);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
    }

    private void check1(Segment segment) {
        verify(segment).putRowCount(3);
        verify(segment).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.newRangeBitmap(0, 3);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        Column column = segment.getColumn(new ColumnKey("a"));
        DetailColumn detail = column.getDetailColumn();
        DictionaryEncodedColumn dict = column.getDictionaryEncodedColumn();
        final BitmapIndexedColumn indexed = column.getBitmapIndex();

        verify(detail).put(0, 2);
        verify(detail).put(1, 1);
        verify(detail).put(2, 3);

        DictionaryEncodedColumn.Putter putter = dict.putter();
        verify(putter).putSize(4);
        verify(putter).putIndex(0, 2);
        verify(putter).putIndex(1, 1);
        verify(putter).putIndex(2, 3);
        verify(putter).putValue(0, null);
        verify(putter).putValue(1, 1);
        verify(putter).putValue(2, 2);
        verify(putter).putValue(3, 3);

        verify(indexed).putBitMapIndex(eq(0), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.EMPTY_IMMUTABLE;
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(1), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(1);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(2), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(0);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(3), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(2);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
    }

    private void check0(Segment segment) {
        verify(segment).putRowCount(6);
        verify(segment).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.newRangeBitmap(0, 6);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        Column column = segment.getColumn(new ColumnKey("a"));
        DetailColumn detail = column.getDetailColumn();
        DictionaryEncodedColumn dict = column.getDictionaryEncodedColumn();
        final BitmapIndexedColumn indexed = column.getBitmapIndex();

        verify(detail).put(0, 1);
        verify(detail).put(1, 2);
        verify(detail).put(2, 3);
        verify(detail).put(3, 1);
        verify(detail).put(4, 5);
        verify(detail).put(5, 3);

        DictionaryEncodedColumn.Putter putter = dict.putter();
        verify(putter).putSize(5);
        verify(putter).putIndex(0, 1);
        verify(putter).putIndex(1, 2);
        verify(putter).putIndex(2, 3);
        verify(putter).putIndex(3, 1);
        verify(putter).putIndex(4, 4);
        verify(putter).putIndex(5, 3);
        verify(putter).putValue(0, null);
        verify(putter).putValue(1, 1);
        verify(putter).putValue(2, 2);
        verify(putter).putValue(3, 3);
        verify(putter).putValue(4, 5);

        verify(indexed).putBitMapIndex(eq(0), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = BitMaps.EMPTY_IMMUTABLE;
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(1), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(0, 3);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(2), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(1);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(3), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(2, 5);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
        verify(indexed).putBitMapIndex(eq(4), argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap bitMap) {
                ImmutableBitMap expected = of(4);
                return expected.getCardinality() == bitMap.getCardinality() && expected.getAndNot(bitMap).isEmpty();
            }
        }));
    }

    private void mockExpectedColumn(Segment segment) {
        Column column = mock(Column.class);
        DetailColumn detail = mock(DetailColumn.class);
        DictionaryEncodedColumn dict = mock(DictionaryEncodedColumn.class);
        BitmapIndexedColumn indexed = mock(BitmapIndexedColumn.class);
        when(column.getDetailColumn()).thenReturn(detail);
        when(column.getDictionaryEncodedColumn()).thenReturn(dict);
        when(column.getBitmapIndex()).thenReturn(indexed);
        when(segment.getColumn(new ColumnKey("a"))).thenReturn(column);

        DictionaryEncodedColumn.Putter putter = mock(DictionaryEncodedColumn.Putter.class);
        when(dict.putter()).thenReturn(putter);
    }

    private Column mockColumn(Column column) {
        DetailColumn detailColumn = mock(DetailColumn.class);
        DictionaryEncodedColumn dictionaryEncodedColumn = mock(DictionaryEncodedColumn.class);
        BitmapIndexedColumn bitmapIndexedColumn = mock(BitmapIndexedColumn.class);

        // rowCount = 6;
        when(detailColumn.get(0)).thenReturn(1);
        when(detailColumn.get(1)).thenReturn(2);
        when(detailColumn.get(2)).thenReturn(3);
        when(detailColumn.get(3)).thenReturn(1);
        when(detailColumn.get(4)).thenReturn(5);
        when(detailColumn.get(5)).thenReturn(3);
        // dict value: [1, 2, 3, 5], index to value:[0:null, 1:1, 2:2, 3:3, 4:5]
        when(dictionaryEncodedColumn.getIndex(null)).thenReturn(0);
        when(dictionaryEncodedColumn.getIndex(1)).thenReturn(1);
        when(dictionaryEncodedColumn.getIndex(2)).thenReturn(2);
        when(dictionaryEncodedColumn.getIndex(3)).thenReturn(3);
        when(dictionaryEncodedColumn.getIndex(5)).thenReturn(4);

        when(dictionaryEncodedColumn.getValue(0)).thenReturn(null);
        when(dictionaryEncodedColumn.getValue(1)).thenReturn(1);
        when(dictionaryEncodedColumn.getValue(2)).thenReturn(2);
        when(dictionaryEncodedColumn.getValue(3)).thenReturn(3);
        when(dictionaryEncodedColumn.getValue(4)).thenReturn(5);

        when(dictionaryEncodedColumn.getIndexByRow(0)).thenReturn(1);
        when(dictionaryEncodedColumn.getIndexByRow(1)).thenReturn(2);
        when(dictionaryEncodedColumn.getIndexByRow(2)).thenReturn(3);
        when(dictionaryEncodedColumn.getIndexByRow(3)).thenReturn(1);
        when(dictionaryEncodedColumn.getIndexByRow(4)).thenReturn(4);
        when(dictionaryEncodedColumn.getIndexByRow(5)).thenReturn(3);

        when(dictionaryEncodedColumn.size()).thenReturn(5);

        // index to bitmap: {0:[], 1:[0, 3], 2:[1], 3:[2, 5], 5:[4]}
        when(bitmapIndexedColumn.getBitMapIndex(0)).thenReturn(BitMaps.EMPTY_IMMUTABLE);
        when(bitmapIndexedColumn.getBitMapIndex(1)).thenReturn(of(0, 3));
        when(bitmapIndexedColumn.getBitMapIndex(2)).thenReturn(of(1));
        when(bitmapIndexedColumn.getBitMapIndex(3)).thenReturn(of(2, 5));
        when(bitmapIndexedColumn.getBitMapIndex(4)).thenReturn(of(4));

        when(column.getDetailColumn()).thenReturn(detailColumn);
        when(column.getDictionaryEncodedColumn()).thenReturn(dictionaryEncodedColumn);
        when(column.getBitmapIndex()).thenReturn(bitmapIndexedColumn);
        return column;
    }

    private ImmutableBitMap of(Integer... bits) {
        MutableBitMap bitMap = BitMaps.newRoaringMutable();
        for (Integer bit : bits) {
            bitMap.add(bit);
        }
        return bitMap;
    }
}