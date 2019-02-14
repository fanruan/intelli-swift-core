package com.fr.swift.segment.column.impl.base;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.input.DoubleReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.DoubleWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.cube.io.output.StringWriter;
import com.fr.swift.cube.io.output.Writer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2017/11/10
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class DetailColumnTest {

    @Mock
    private IntWriter intWriter;
    @Mock
    private LongWriter longWriter;
    @Mock
    private DoubleWriter doubleWriter;
    @Mock
    private StringWriter stringWriter;

    @Mock
    private IntReader intReader;
    @Mock
    private LongReader longReader;
    @Mock
    private DoubleReader doubleReader;
    @Mock
    private StringReader stringReader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        IResourceDiscovery resourceDiscovery = mock(IResourceDiscovery.class);
        Whitebox.setInternalState(BaseDetailColumn.class, "DISCOVERY", resourceDiscovery);

        when(resourceDiscovery.getReader(ArgumentMatchers.<ResourceLocation>any(), ArgumentMatchers.<BuildConf>any())).then(new Answer<Reader>() {
            @Override
            public Reader answer(InvocationOnMock invocation) throws Throwable {
                switch (invocation.<BuildConf>getArgument(1).getDataType()) {
                    case INT:
                        return intReader;
                    case LONG:
                        return longReader;
                    case DOUBLE:
                        return doubleReader;
                    case STRING:
                        return stringReader;
                    default:
                        return null;
                }
            }
        });

        when(resourceDiscovery.getWriter(ArgumentMatchers.<ResourceLocation>any(), ArgumentMatchers.<BuildConf>any())).then(new Answer<Writer>() {
            @Override
            public Writer answer(InvocationOnMock invocation) throws Throwable {
                switch (invocation.<BuildConf>getArgument(1).getDataType()) {
                    case INT:
                        return intWriter;
                    case LONG:
                        return longWriter;
                    case DOUBLE:
                        return doubleWriter;
                    case STRING:
                        return stringWriter;
                    default:
                        return null;
                }
            }
        });

        when(intReader.isReadable()).thenReturn(true);
        when(intReader.get(anyLong())).thenReturn(1);
        when(longReader.isReadable()).thenReturn(true);
        when(longReader.get(anyLong())).thenReturn(1L);
        when(doubleReader.isReadable()).thenReturn(true);
        when(doubleReader.get(anyLong())).thenReturn(1D);
        when(stringReader.isReadable()).thenReturn(true);
        when(stringReader.get(anyLong())).thenReturn("1");

        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftCubePathService swiftCubePathService = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(swiftCubePathService);
        when(swiftCubePathService.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void put() {
        ResourceLocation location = new ResourceLocation("");
        new IntDetailColumn(location).put(0, 1);
        new LongDetailColumn(location).put(0, 1L);
        new DoubleDetailColumn(location).put(0, 1D);
        new StringDetailColumn(location).put(0, "1");

        verify(intWriter).put(0, 1);
        verify(longWriter).put(0, 1);
        verify(doubleWriter).put(0, 1);
        verify(stringWriter).put(0, "1");
    }

    @Test
    public void get() {
        ResourceLocation location = new ResourceLocation("");
        IntDetailColumn intDetailColumn = new IntDetailColumn(location);
        LongDetailColumn longDetailColumn = new LongDetailColumn(location);
        DoubleDetailColumn doubleDetailColumn = new DoubleDetailColumn(location);
        StringDetailColumn stringDetailColumn = new StringDetailColumn(location);

        assertEquals(1, intDetailColumn.get(0).intValue());
        assertEquals(1, longDetailColumn.get(1).longValue());
        assertEquals(1, doubleDetailColumn.get(2), 0D);
        assertEquals("1", stringDetailColumn.get(2));

        verify(intReader).get(anyLong());
    }

    @Test
    public void isReadable() {
        ResourceLocation location = new ResourceLocation("");
        assertTrue(new IntDetailColumn(location).isReadable());
        assertTrue(new LongDetailColumn(location).isReadable());
        assertTrue(new DoubleDetailColumn(location).isReadable());
        assertTrue(new StringDetailColumn(location).isReadable());

        verify(intReader).isReadable();
    }

    @Test
    public void release() {
        ResourceLocation location = new ResourceLocation("");
        IntDetailColumn intDetailColumn = new IntDetailColumn(location);
        intDetailColumn.put(0, 1);
        intDetailColumn.get(0);
        intDetailColumn.release();

        LongDetailColumn longDetailColumn = new LongDetailColumn(location);
        longDetailColumn.put(0, 1L);
        longDetailColumn.get(0);
        longDetailColumn.release();

        DoubleDetailColumn doubleDetailColumn = new DoubleDetailColumn(location);
        doubleDetailColumn.put(0, 1D);
        doubleDetailColumn.get(0);
        doubleDetailColumn.release();

        StringDetailColumn stringDetailColumn = new StringDetailColumn(location);
        stringDetailColumn.put(0, "1");
        stringDetailColumn.get(0);
        stringDetailColumn.release();

        verify(intWriter).release();
        verify(intReader).release();

        verify(longWriter).release();
        verify(longReader).release();

        verify(doubleWriter).release();
        verify(doubleReader).release();

        verify(stringWriter).release();
        verify(stringReader).release();
    }
}
