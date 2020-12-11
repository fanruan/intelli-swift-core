package com.fr.swift.segment.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.CacheColumnSegment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/1/11
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftInserter.class, CubeUtil.class, SwiftContext.class, SwiftDatabase.class, SwiftEventDispatcher.class, HistoryBlockImporter.class, SegmentUtils.class})
public class HistoryBlockImporterTest {

    @Before
    public void setUp() {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        when(SwiftContext.get().getBean(SwiftCubePathService.class)).thenReturn(mock(SwiftCubePathService.class));
    }

    @Test
    public void getInserting() throws Exception {
        SegmentKey segKey = mock(SegmentKey.class, Mockito.RETURNS_DEEP_STUBS);

        mockStatic(CubeUtil.class);
        when(segKey.getSegmentUri()).thenReturn("1");

        CubePathBuilder cubePathBuilder = mock(CubePathBuilder.class, Mockito.RETURNS_DEEP_STUBS);
        whenNew(CubePathBuilder.class).withArguments(segKey).thenReturn(cubePathBuilder);

        ResourceLocation location = mock(ResourceLocation.class);
        whenNew(ResourceLocation.class).withArguments(
                cubePathBuilder.setTempDir(segKey.getSegmentUri()).build(),
                segKey.getStoreType()).thenReturn(location);

        DataSource dataSource = mock(DataSource.class, Mockito.RETURNS_DEEP_STUBS);
        CacheColumnSegment seg = mock(CacheColumnSegment.class);
        whenNew(CacheColumnSegment.class).withArguments(location, dataSource.getMetadata()).thenReturn(seg);

        SwiftInserter inserter = mock(SwiftInserter.class);
        mockStatic(SwiftInserter.class);
        when(SwiftInserter.ofOverwriteMode(seg)).thenReturn(inserter);

        HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>> historyBlockImporter = new HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>>(dataSource, mock(SwiftSourceAlloter.class));
        Object inserting = historyBlockImporter.getInserting(segKey);

        assertEquals(inserter, Whitebox.getInternalState(inserting, "inserter"));
        assertEquals(seg, Whitebox.getInternalState(inserting, "seg"));
        assertEquals(0, (int) Whitebox.getInternalState(inserting, "rowCount"));

    }

    @Test
    public void handleFullSegment() throws Exception {
        SegmentInfo segInfo = mock(SegmentInfo.class);
        HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>> historyBlockImporter = spy(new HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>>(mock(DataSource.class), mock(SwiftSourceAlloter.class)));

        SegmentKey segKey = mock(SegmentKey.class);
        doReturn(segKey).when(historyBlockImporter, "newSegmentKey", segInfo);

        mockStatic(SwiftEventDispatcher.class);

        historyBlockImporter.handleFullSegment(segInfo);

        verifyStatic(SwiftEventDispatcher.class);
        SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, segKey);
    }

    @Test
    public void indexIfNeed() {
        // FIXME 2019/3/13 anchore 私有内部类限制，无法测试
//        SegmentInfo segInfo = mock(SegmentInfo.class);
//        HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>> historyBlockImporter = spy(new HistoryBlockImporter<SwiftSourceAlloter<?, RowInfo>>(mock(DataSource.class), mock(SwiftSourceAlloter.class)));
//        Map<SegmentInfo, ?> insertings = mock(Map.class, Mockito.RETURNS_DEEP_STUBS);
//        Whitebox.setInternalState(historyBlockImporter, "insertings", insertings);
//        when(insertings.get(segInfo))
//        historyBlockImporter.indexIfNeed(segInfo);
    }

    @Test
    public void clearDirtyIfNeed() {
        // TODO: 2019/3/12 anchore
    }
}