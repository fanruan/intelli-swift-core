package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.impl.LongColumn;
import com.fr.swift.segment.column.impl.base.IResourceDiscovery;
import com.fr.swift.segment.column.impl.empty.ImmutableNullColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/1/12
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({ColumnTypeUtils.class, SwiftContext.class, HistorySegmentImpl.class, BaseSegment.class})
public class HistorySegmentImplTest {

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
        when(resourceDiscovery.getReader(ArgumentMatchers.<IResourceLocation>any(), ArgumentMatchers.<BuildConf>any())).thenAnswer(new Answer<Reader>() {
            @Override
            public Reader answer(InvocationOnMock invocation) throws Throwable {
                switch (invocation.<BuildConf>getArgument(1).getDataType()) {
                    case INT:
                        return intReader;
                    case BITMAP:
                        return bitmapReader;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        });
        when(resourceDiscovery.getWriter(ArgumentMatchers.<IResourceLocation>any(), ArgumentMatchers.<BuildConf>any())).thenAnswer(new Answer<Writer>() {
            @Override
            public Writer answer(InvocationOnMock invocation) throws Throwable {
                switch (invocation.<BuildConf>getArgument(1).getDataType()) {
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
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);
        SwiftCubePathService service = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(service);
        when(service.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void newColumn() throws Exception {
        IResourceLocation location = mock(IResourceLocation.class);
        HistorySegmentImpl seg = spy(new HistorySegmentImpl(location, mock(SwiftMetaData.class)));

        doReturn(false).when(seg).isReadable();
        assertTrue(seg.newColumn(location, ClassType.LONG) instanceof LongColumn);

        doReturn(true).when(seg).isReadable();
        LongColumn longColumn = mock(LongColumn.class);
        whenNew(LongColumn.class).withAnyArguments().thenReturn(longColumn);
        DetailColumn detailColumn = mock(DetailColumn.class);
        when(longColumn.getDetailColumn()).thenReturn(detailColumn);
        when(detailColumn.isReadable()).thenReturn(false);
        assertTrue(seg.newColumn(location, ClassType.LONG) instanceof ImmutableNullColumn<?>);
    }

    @Test
    public void getAllShowIndex() {
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);
        when(bitmapReader.get(0)).thenReturn(bitmap, mock(ImmutableBitMap.class));

        HistorySegmentImpl seg = new HistorySegmentImpl(new ResourceLocation("/"), mock(SwiftMetaData.class));
        assertEquals(bitmap, seg.getAllShowIndex());
        // cache all show
        assertEquals(bitmap, seg.getAllShowIndex());
    }

    @Test
    public void release() {
        HistorySegmentImpl seg = new HistorySegmentImpl(new ResourceLocation("/"), mock(SwiftMetaData.class));
        seg.putRowCount(3);
        seg.getRowCount();
        seg.putAllShowIndex(mock(ImmutableBitMap.class));
        seg.getAllShowIndex();

        seg.release();

        verify(intReader).release();
        verify(intWriter).release();
        verify(bitmapReader).release();
        verify(bitmapWriter).release();

        assertNull(Whitebox.getInternalState(seg, "allShowBitMapCache"));
    }
}