package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2018/8/1
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class SegmentResultSetTest {

    private final SwiftMetaData meta = new SwiftMetaDataBean("DEMO_CONTRACT",
            Collections.<SwiftMetaDataColumn>singletonList(
                    new MetaDataColumnBean("合同ID", Types.VARCHAR)));

    @Mock
    private Segment seg;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftCubePathService swiftCubePathService = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(swiftCubePathService);
        when(swiftCubePathService.getSwiftPath()).thenReturn("/");

        when(seg.isHistory()).thenReturn(true);
        when(seg.isReadable()).thenReturn(true);
        when(seg.getMetaData()).thenReturn(meta);
        when(seg.getRowCount()).thenReturn(4);
        when(seg.getAllShowIndex()).thenReturn(new RangeBitmap(1, 4));

        Column column = mock(Column.class);
        when(seg.getColumn(new ColumnKey("合同ID"))).thenReturn(column);

        IResourceLocation location = mock(IResourceLocation.class);
        when(column.getLocation()).thenReturn(location);
        when(location.getStoreType()).thenReturn(StoreType.FINE_IO);

        DetailColumn detailColumn = mock(DetailColumn.class);
        when(column.getDetailColumn()).thenReturn(detailColumn);
        when(column.getDictionaryEncodedColumn()).thenReturn(mock(DictionaryEncodedColumn.class));

        when(detailColumn.get(0)).thenReturn("0");
        when(detailColumn.get(1)).thenReturn("1");
        when(detailColumn.get(2)).thenReturn("2");
        when(detailColumn.get(3)).thenReturn("\0");

        BitmapIndexedColumn bitmapIndex = mock(BitmapIndexedColumn.class);
        when(column.getBitmapIndex()).thenReturn(bitmapIndex);
        when(bitmapIndex.getNullIndex()).thenReturn(new RangeBitmap(3, 4));
    }

    @Test
    public void test() throws Exception {
        SegmentResultSet resultSet = new SegmentResultSet(seg);

        assertEquals(seg.getMetaData(), resultSet.getMetaData());

        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }
        resultSet.close();

        verify(seg).release();

        Column<Object> column = seg.getColumn(new ColumnKey("合同ID"));
        DetailColumn<Object> detailColumn = column.getDetailColumn();
        DictionaryEncodedColumn<Object> dictionaryEncodedColumn = column.getDictionaryEncodedColumn();
        BitmapIndexedColumn bitmapIndex = column.getBitmapIndex();

        verify(detailColumn).release();
        verify(dictionaryEncodedColumn).release();
        verify(bitmapIndex).release();

        assertEquals(3, rows.size());
        assertEquals("1", rows.get(0).getValue(0));
        assertEquals("2", rows.get(1).getValue(0));
        assertNull(rows.get(2).getValue(0));
    }
}