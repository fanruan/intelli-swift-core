package com.fr.swift.segment.column.impl.base;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.compare.Comparators;
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
import com.fr.swift.util.ArrayLookupHelper;
import com.fr.swift.util.ArrayLookupHelper.Lookup;
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

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/6
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, ArrayLookupHelper.class})
public class DictColumnTest {

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
        IResourceDiscovery resourceDiscovery = mock(IResourceDiscovery.class);
        Whitebox.setInternalState(BaseDictColumn.class, "DISCOVERY", resourceDiscovery);

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

        when(intReader.get(anyLong())).thenReturn(1);
        when(longReader.get(anyLong())).thenReturn(1L);
        when(doubleReader.get(anyLong())).thenReturn(1D);
        when(stringReader.get(anyLong())).thenReturn("1");

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

        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftCubePathService swiftCubePathService = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(swiftCubePathService);
        when(swiftCubePathService.getSwiftPath()).thenReturn("/");

        mockStatic(ArrayLookupHelper.class);
        when(ArrayLookupHelper.lookup(ArgumentMatchers.<Object[]>any(), ArgumentMatchers.<Lookup<Object>>any())).thenReturn(new int[]{1});
    }

    @Test
    public void size() {
        ResourceLocation location = new ResourceLocation("");
        assertEquals(1, new IntDictColumn(location, Comparators.<Integer>asc()).size());
    }

    @Test
    public void getIndexByRow() {
        ResourceLocation location = new ResourceLocation("");
        assertEquals(1, new IntDictColumn(location, Comparators.<Integer>asc()).getIndexByRow(0));
    }

    @Test
    public void getIndex() {
        ResourceLocation location = new ResourceLocation("");
        assertEquals(0, new IntDictColumn(location, Comparators.<Integer>asc()).getIndex(null));
        assertEquals(0, new StringDictColumn(location, Comparators.<String>asc()).getIndex(""));

        assertEquals(1, new IntDictColumn(location, Comparators.<Integer>asc()).getIndex(1));
    }

    @Test
    public void getComparator() {
        ResourceLocation location = new ResourceLocation("");
        Comparator<Integer> comparator = Comparators.asc();
        assertEquals(comparator, new IntDictColumn(location, comparator).getComparator());
    }

    @Test
    public void isReadable() {
        ResourceLocation location = new ResourceLocation("");
        new IntDictColumn(location, Comparators.<Integer>asc()).isReadable();

        verify(intReader).isReadable();
        verify(intReader).release();
    }

    @Test
    public void release() {
        ResourceLocation location = new ResourceLocation("");
        IntDictColumn intDictColumn = new IntDictColumn(location, Comparators.<Integer>asc());
        intDictColumn.release();
        verifyZeroInteractions(intWriter);
        verifyZeroInteractions(intReader);

        intDictColumn.putter().putSize(2);
        intDictColumn.putter().putIndex(0, 1);
        intDictColumn.putter().putValue(1, 1);
        intDictColumn.putter().putGlobalSize(2);
        intDictColumn.putter().putGlobalIndex(0, 1);

        intDictColumn.size();
        intDictColumn.getIndexByRow(0);
        intDictColumn.getValue(1);
        intDictColumn.globalSize();
        intDictColumn.getGlobalIndexByIndex(1);

        intDictColumn.release();

        verify(intWriter, times(5)).release();
        verify(intReader, times(5)).release();
    }

    @Test
    public void getValueByRow() {
        ResourceLocation location = new ResourceLocation("");

        assertEquals(1, new IntDictColumn(location, Comparators.<Integer>asc()).getValueByRow(0).intValue());
    }

    @Test
    public void putValue() {
        ResourceLocation location = new ResourceLocation("");
        new IntDictColumn(location, Comparators.<Integer>asc()).putter().putValue(1, 1);
        new LongDictColumn(location, Comparators.<Long>asc()).putter().putValue(1, 1L);
        new DoubleDictColumn(location, Comparators.<Double>asc()).putter().putValue(1, 1D);
        new StringDictColumn(location, Comparators.<String>asc()).putter().putValue(1, "1");

        verify(intWriter).put(1, 1);
        verify(longWriter).put(1, 1);
        verify(doubleWriter).put(1, 1);
        verify(stringWriter).put(1, "1");
    }

    @Test
    public void getValue() {
        ResourceLocation location = new ResourceLocation("");
        IntDictColumn intDictColumn = new IntDictColumn(location, Comparators.<Integer>asc());
        LongDictColumn longDictColumn = new LongDictColumn(location, Comparators.<Long>asc());
        DoubleDictColumn doubleDictColumn = new DoubleDictColumn(location, Comparators.<Double>asc());
        StringDictColumn stringDictColumn = new StringDictColumn(location, Comparators.<String>asc());

        assertNull(intDictColumn.getValue(0));
        assertNull(longDictColumn.getValue(0));
        assertNull(doubleDictColumn.getValue(0));
        assertNull(stringDictColumn.getValue(0));

        assertEquals(1, intDictColumn.getValue(1).intValue());
        assertEquals(1, longDictColumn.getValue(1).longValue());
        assertEquals(1, doubleDictColumn.getValue(1), 0);
        assertEquals("1", stringDictColumn.getValue(1));
    }
}