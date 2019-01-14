package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IResourceDiscovery;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/12
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({ColumnTypeUtils.class, SwiftContext.class, MutableHistorySegment.class})
public class MutableHistorySegmentTest {

    private final String path = "/";
    @Mock
    private IntReader intReader;
    @Mock
    private BitMapReader bitmapReader;
    @Mock
    private IntWriter intWriter;
    @Mock
    private BitMapWriter bitmapWriter;

    @Before
    public void setUp() throws Exception {
        IResourceDiscovery resourceDiscovery = mock(IResourceDiscovery.class);
        Whitebox.setInternalState(BaseSegment.class, "DISCOVERY", resourceDiscovery);
        when(resourceDiscovery.getReader(Matchers.<IResourceLocation>any(), Matchers.<BuildConf>any())).thenAnswer(new Answer<Reader>() {
            @Override
            public Reader answer(InvocationOnMock invocation) throws Throwable {
                switch (invocation.getArgumentAt(1, BuildConf.class).getDataType()) {
                    case INT:
                        return intReader;
                    case BITMAP:
                        return bitmapReader;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        });
        when(resourceDiscovery.getWriter(Matchers.<IResourceLocation>any(), Matchers.<BuildConf>any())).thenAnswer(new Answer<Writer>() {
            @Override
            public Writer answer(InvocationOnMock invocation) throws Throwable {
                switch (invocation.getArgumentAt(1, BuildConf.class).getDataType()) {
                    case INT:
                        return intWriter;
                    case BITMAP:
                        return bitmapWriter;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        });

        mockStatic(ColumnTypeUtils.class, SwiftContext.class);
        when(ColumnTypeUtils.getClassType(Matchers.<SwiftMetaDataColumn>any())).thenReturn(ClassType.DOUBLE);

        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);
        SwiftCubePathService service = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(service);
        when(service.getSwiftPath()).thenReturn(path);
    }

    @Test
    public void getColumn() throws Exception {
        SwiftMetaData meta = mock(SwiftMetaData.class);
        when(meta.getColumnId("c")).thenReturn("cId");

        MutableHistorySegment seg = spy(new MutableHistorySegment(new ResourceLocation(path), meta));

        Column column = mock(Column.class);
        doReturn(column).when(seg, "newColumn", any(), any());

        assertEquals(column, seg.getColumn(new ColumnKey("c")));
        // cache column
        assertEquals(seg.getColumn(new ColumnKey("c")), seg.getColumn(new ColumnKey("c")));
        // return null if encountered ex
        when(meta.getColumnId("d")).thenThrow(Exception.class);
        assertNull(seg.getColumn(new ColumnKey("d")));
    }

    @Test
    public void getMetaData() {
        SwiftMetaData meta = mock(SwiftMetaData.class);
        assertEquals(meta, new MutableHistorySegment(mock(IResourceLocation.class), meta).getMetaData());
    }

    @Test
    public void getLocation() {
        IResourceLocation location = mock(IResourceLocation.class);
        assertEquals(location, new MutableHistorySegment(location, mock(SwiftMetaData.class)).getLocation());
    }

    @Test
    public void putRowCount() {
        new MutableHistorySegment(new ResourceLocation(path), mock(SwiftMetaData.class)).putRowCount(3);

        verify(intWriter).put(0, 3);
    }

    @Test
    public void getRowCount() {
        when(intReader.get(0)).thenReturn(3);

        assertEquals(3, new MutableHistorySegment(new ResourceLocation(path), mock(SwiftMetaData.class)).getRowCount());
    }

    @Test
    public void putAllShowIndex() {
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);
        new MutableHistorySegment(new ResourceLocation(path), mock(SwiftMetaData.class)).putAllShowIndex(bitmap);

        verify(bitmapWriter).put(0, bitmap);
    }

    @Test
    public void getAllShowIndex() {
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);
        when(bitmapReader.get(0)).thenReturn(bitmap);

        MutableHistorySegment seg = new MutableHistorySegment(new ResourceLocation(path), mock(SwiftMetaData.class));
        assertEquals(bitmap, seg.getAllShowIndex());
    }

    @Test
    public void isReadable() {
        MutableHistorySegment seg = spy(new MutableHistorySegment(new ResourceLocation(path), mock(SwiftMetaData.class)));
        doReturn(true).when(seg).isHistory();

        when(intReader.isReadable()).thenReturn(false);
        assertFalse(seg.isReadable());
        verify(intReader).release();
        verifyZeroInteractions(bitmapReader);

        when(intReader.isReadable()).thenReturn(true);
        when(bitmapReader.isReadable()).thenReturn(false);
        assertFalse(seg.isReadable());
        verify(intReader, times(2)).release();
        verify(bitmapReader).release();

        when(intReader.isReadable()).thenReturn(true);
        when(bitmapReader.isReadable()).thenReturn(true);
        assertTrue(seg.isReadable());
        verify(intReader, times(3)).release();
        verify(bitmapReader, times(2)).release();
    }

    @Test
    public void release() {
        MutableHistorySegment seg = new MutableHistorySegment(new ResourceLocation(path), mock(SwiftMetaData.class));
        seg.putRowCount(3);
        seg.getRowCount();
        seg.putAllShowIndex(mock(ImmutableBitMap.class));
        seg.getAllShowIndex();

        seg.release();

        verify(intReader).release();
        verify(intWriter).release();
        verify(bitmapReader).release();
        verify(bitmapWriter).release();
    }

    @Test
    public void isHistory() {
        IResourceLocation location = mock(IResourceLocation.class);

        MutableHistorySegment seg = new MutableHistorySegment(location, mock(SwiftMetaData.class));

        for (StoreType storeType : StoreType.values()) {
            when(location.getStoreType()).thenReturn(storeType);
            assertEquals(location.getStoreType().isPersistent(), seg.isHistory());
        }
    }
}