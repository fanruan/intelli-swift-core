package com.fr.swift.segment.column.impl.base;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitmapAssert;
import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.Writer;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2017/11/10
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({ResourceDiscovery.class, SwiftContext.class})
public class BitMapColumnTest {

    @Mock
    private BitMapWriter bitmapWriter;
    @Mock
    private BitMapReader bitmapReader;

    @Before
    public void setUp() throws Exception {
        IResourceDiscovery resourceDiscovery = mock(IResourceDiscovery.class);
        Whitebox.setInternalState(BitMapColumn.class, "DISCOVERY", resourceDiscovery);

        when(bitmapReader.isReadable()).thenReturn(true);
        when(bitmapReader.get(anyLong())).thenReturn(new RangeBitmap(1, 3));
        when(resourceDiscovery.getReader(ArgumentMatchers.<ResourceLocation>any(), ArgumentMatchers.<BuildConf>any())).then(new Answer<Reader>() {
            @Override
            public Reader answer(InvocationOnMock invocation) throws Throwable {
                return bitmapReader;
            }
        });

        when(resourceDiscovery.getWriter(ArgumentMatchers.<ResourceLocation>any(), ArgumentMatchers.<BuildConf>any())).then(new Answer<Writer>() {
            @Override
            public Writer answer(InvocationOnMock invocation) throws Throwable {
                return bitmapWriter;
            }
        });

        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftCubePathService swiftCubePathService = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(swiftCubePathService);
        when(swiftCubePathService.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void putNullIndex() {
        new BitMapColumn(new ResourceLocation("")).putNullIndex(new EmptyBitmap());

        verify(bitmapWriter).resetContentPosition();
        verify(bitmapWriter).put(anyLong(), ArgumentMatchers.<ImmutableBitMap>any());
    }

    @Test
    public void getNullIndex() {
        BitmapAssert.equals(new RangeBitmap(1, 3), new BitMapColumn(new ResourceLocation("")).getNullIndex());
    }

    @Test
    public void putBitMapIndex() {
        new BitMapColumn(new ResourceLocation("")).putBitMapIndex(0, new EmptyBitmap());

        verify(bitmapWriter).put(anyLong(), ArgumentMatchers.<ImmutableBitMap>any());
    }

    @Test
    public void getBitMapIndex() {
        BitmapAssert.equals(new RangeBitmap(1, 3), new BitMapColumn(new ResourceLocation("")).getBitMapIndex(0));
    }

    @Test
    public void isReadable() {
        new BitMapColumn(new ResourceLocation("")).isReadable();

        verify(bitmapReader).isReadable();
        verify(bitmapReader).release();
    }

    @Test
    public void release() {
        BitMapColumn bitMapColumn = new BitMapColumn(new ResourceLocation(""));
        bitMapColumn.release();

        verifyZeroInteractions(bitmapReader, bitmapWriter);

        bitMapColumn.putNullIndex(new EmptyBitmap());
        bitMapColumn.getNullIndex();
        bitMapColumn.release();

        verify(bitmapWriter).release();
        verify(bitmapReader).release();
    }
}
