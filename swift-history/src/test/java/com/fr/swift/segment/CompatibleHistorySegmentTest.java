package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.context.ContextProvider;
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
import com.fr.swift.segment.column.impl.IntColumn;
import com.fr.swift.segment.column.impl.LongColumn;
import com.fr.swift.segment.column.impl.base.IResourceDiscovery;
import com.fr.swift.segment.column.impl.empty.ReadonlyNullColumn;
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
@PrepareForTest({ColumnTypeUtils.class, SwiftContext.class, CompatibleHistorySegment.class, BaseSegment.class})
public class CompatibleHistorySegmentTest {

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
        final ContextProvider mock = mock(ContextProvider.class);
        when(mock.getContextPath()).thenReturn("/");
        when(beanFactory.getBean(ContextProvider.class)).thenReturn(mock);

        SwiftConfig service = mock(SwiftConfig.class);
        when(beanFactory.getBean(SwiftConfig.class)).thenReturn(service);
        final SwiftConfigEntityQueryBus query = mock(SwiftConfigEntityQueryBus.class);
        when(service.query(SwiftConfigEntity.class)).thenReturn(query);
        when(query.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, "/")).thenReturn("/");
    }

    @Test
    public void newIntColumn() throws Exception {
        IResourceLocation location = mock(IResourceLocation.class);
        CompatibleHistorySegment seg = spy(new CompatibleHistorySegment(location, mock(SwiftMetaData.class)));

        doReturn(false).when(seg).isReadable();
        assertTrue(seg.newColumn(location, ClassType.INTEGER) instanceof IntColumn);

        doReturn(true).when(seg).isReadable();
        IntColumn intColumn = mock(IntColumn.class);
        whenNew(IntColumn.class).withAnyArguments().thenReturn(intColumn);
        DetailColumn detailColumn = mock(DetailColumn.class);
        when(intColumn.getDetailColumn()).thenReturn(detailColumn);
        when(detailColumn.isReadable()).thenReturn(false);
        assertTrue(seg.newColumn(location, ClassType.INTEGER) instanceof ReadonlyNullColumn<?>);
    }

    @Test
    public void newLongColumn() throws Exception {
        IResourceLocation location = mock(IResourceLocation.class);
        CompatibleHistorySegment seg = spy(new CompatibleHistorySegment(location, mock(SwiftMetaData.class)));

        doReturn(false).when(seg).isReadable();
        assertTrue(seg.newColumn(location, ClassType.LONG) instanceof LongColumn);

        doReturn(true).when(seg).isReadable();
        LongColumn longColumn = mock(LongColumn.class);
        whenNew(LongColumn.class).withAnyArguments().thenReturn(longColumn);
        DetailColumn detailColumn = mock(DetailColumn.class);
        when(longColumn.getDetailColumn()).thenReturn(detailColumn);
        when(detailColumn.isReadable()).thenReturn(false);
        assertTrue(seg.newColumn(location, ClassType.LONG) instanceof ReadonlyNullColumn<?>);
    }

    @Test
    public void getAllShowIndex() {
        ImmutableBitMap bitmap = mock(ImmutableBitMap.class);
        when(bitmapReader.get(0)).thenReturn(bitmap, mock(ImmutableBitMap.class));

        CompatibleHistorySegment seg = new CompatibleHistorySegment(new ResourceLocation("/"), mock(SwiftMetaData.class));
        assertEquals(bitmap, seg.getAllShowIndex());
        // cache all show
        assertEquals(bitmap, seg.getAllShowIndex());
    }

    @Test
    public void release() {
        CompatibleHistorySegment seg = new CompatibleHistorySegment(new ResourceLocation("/"), mock(SwiftMetaData.class));
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