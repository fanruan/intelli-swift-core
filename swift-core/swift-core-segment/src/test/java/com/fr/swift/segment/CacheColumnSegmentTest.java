package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/12
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({ColumnTypeUtils.class, SwiftContext.class, CacheColumnSegment.class, SegmentUtils.class})
public class CacheColumnSegmentTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(ColumnTypeUtils.class, SwiftContext.class, SegmentUtils.class);
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

        CacheColumnSegment seg = spy(new CacheColumnSegment(new ResourceLocation("/", "D:"), meta));

        Column column = mock(Column.class);
        doReturn(column).when(seg, "newColumn", any(), any());

        Assert.assertEquals(column, seg.getColumn(new ColumnKey("c")));
        // cache column
        Assert.assertEquals(seg.getColumn(new ColumnKey("c")), seg.getColumn(new ColumnKey("c")));
        // return null if encountered ex
        when(meta.getColumnId("d")).thenThrow(Exception.class);
        assertNull(seg.getColumn(new ColumnKey("d")));
    }

    @Test
    public void release() throws Exception {
        SwiftMetaData meta = mock(SwiftMetaData.class);
        when(meta.getColumnId("c")).thenReturn("cId");

        CacheColumnSegment seg = new CacheColumnSegment(new ResourceLocation("/", "D:"), meta);

        seg.getColumn(new ColumnKey("cId"));

        seg.release();

        Map<ColumnKey, Column<?>> columns = Whitebox.getInternalState(seg, "columns");
        verifyStatic(SegmentUtils.class);
        SegmentUtils.releaseHisColumn(columns.values());
    }
}