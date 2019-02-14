package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.RealtimeDateColumn;
import com.fr.swift.segment.column.RealtimeDoubleColumn;
import com.fr.swift.segment.column.RealtimeLongColumn;
import com.fr.swift.segment.column.RealtimeStringColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
@PrepareForTest({ColumnTypeUtils.class, SwiftContext.class, RealtimeSegmentImpl.class})
public class RealtimeSegmentImplTest {

    @Before
    public void setUp() throws Exception {
        whenNew(RealtimeDoubleColumn.class).withAnyArguments().thenReturn(mock(RealtimeDoubleColumn.class), mock(RealtimeDoubleColumn.class));

        mockStatic(ColumnTypeUtils.class, SwiftContext.class);

        when(ColumnTypeUtils.getClassType(ArgumentMatchers.<SwiftMetaDataColumn>any())).thenReturn(ClassType.DOUBLE);

        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);
        SwiftCubePathService service = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(service);
        when(service.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void getColumn() throws Exception {
        SwiftMetaData meta = mock(SwiftMetaData.class);
        when(meta.getColumnId("c")).thenReturn("cId");

        RealtimeSegmentImpl seg = spy(new RealtimeSegmentImpl(new ResourceLocation("/"), meta));

        // no cache of column
        assertNotEquals(seg.getColumn(new ColumnKey("c")), seg.getColumn(new ColumnKey("c")));
        // return null if encountered ex
        when(meta.getColumnId("d")).thenThrow(Exception.class);
        assertNull(seg.getColumn(new ColumnKey("d")));
    }

    @Test
    public void newColumn() throws Exception {
        whenNew(RealtimeLongColumn.class).withAnyArguments().thenReturn(mock(RealtimeLongColumn.class), mock(RealtimeLongColumn.class));
        whenNew(RealtimeStringColumn.class).withAnyArguments().thenReturn(mock(RealtimeStringColumn.class), mock(RealtimeStringColumn.class));
        whenNew(RealtimeDateColumn.class).withAnyArguments().thenReturn(mock(RealtimeDateColumn.class), mock(RealtimeDateColumn.class));

        IResourceLocation location = mock(IResourceLocation.class);
        RealtimeSegmentImpl seg = spy(new RealtimeSegmentImpl(location, mock(SwiftMetaData.class)));

        assertTrue(seg.newColumn(location, ClassType.INTEGER) instanceof RealtimeLongColumn);
        assertTrue(seg.newColumn(location, ClassType.LONG) instanceof RealtimeLongColumn);
        assertTrue(seg.newColumn(location, ClassType.DOUBLE) instanceof RealtimeDoubleColumn);
        assertTrue(seg.newColumn(location, ClassType.STRING) instanceof RealtimeStringColumn);
        assertTrue(seg.newColumn(location, ClassType.DATE) instanceof RealtimeDateColumn);
    }
}