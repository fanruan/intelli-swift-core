package com.fr.swift.cloud.segment.backup;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.beans.factory.BeanFactory;
import com.fr.swift.cloud.cube.CubePathBuilder;
import com.fr.swift.cloud.cube.io.Types.StoreType;
import com.fr.swift.cloud.cube.io.location.ResourceLocation;
import com.fr.swift.cloud.segment.BackupSegment;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.operator.insert.SwiftInserter;
import com.fr.swift.cloud.source.DataSource;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;
import com.fr.swift.cloud.transaction.TransactionManager;
import com.fr.swift.cloud.transaction.TransactionProxyFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/3/12
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SegmentUtils.class, BackupBlockImporter.class, SwiftContext.class, SwiftInserter.class})
public class BackupBlockImporterTest {

    @Test
    public void getInserting() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        // mock realtime seg
        SegmentKey segKey = mock(SegmentKey.class);
        Segment realtimeSeg = mock(Segment.class);
        mockStatic(SegmentUtils.class);
        when(SegmentUtils.newSegment(segKey)).thenReturn(realtimeSeg);

        CubePathBuilder cubePathBuilder = mock(CubePathBuilder.class, RETURNS_DEEP_STUBS);
        whenNew(CubePathBuilder.class).withArguments(segKey).thenReturn(cubePathBuilder);

        ResourceLocation location = mock(ResourceLocation.class);
        whenNew(ResourceLocation.class).withArguments(eq(cubePathBuilder.asBackup().build()), eq(StoreType.NIO)).thenReturn(location);
        // mock backup seg
        DataSource dataSource = mock(DataSource.class, RETURNS_DEEP_STUBS);
        BackupSegment backupSeg = mock(BackupSegment.class);
        whenNew(BackupSegment.class).withArguments(location, dataSource.getMetadata()).thenReturn(backupSeg);
        // mock tx
        TransactionManager transactionManager = mock(TransactionManager.class);
        when(SwiftContext.get().getBean("transactionManager", TransactionManager.class, backupSeg)).thenReturn(transactionManager);

        TransactionProxyFactory proxyFactory = mock(TransactionProxyFactory.class);
        whenNew(TransactionProxyFactory.class).withArguments(transactionManager).thenReturn(proxyFactory);
        SwiftInserter inserter = mock(SwiftInserter.class);
        mockStatic(SwiftInserter.class);
        when(SwiftInserter.ofAppendMode(backupSeg)).thenReturn(inserter);
        SwiftInserter proxyInserter = mock(SwiftInserter.class);
        when(proxyFactory.getProxy(inserter)).thenReturn(proxyInserter);

        SwiftSourceAlloter alloter = mock(SwiftSourceAlloter.class);

        when(backupSeg.isReadable()).thenReturn(true);
        when(backupSeg.getRowCount()).thenReturn(1);

        Object inserting = new BackupBlockImporter(dataSource, alloter).getInserting(segKey);

        verify(transactionManager).setOldAttach(realtimeSeg);
        verify(proxyFactory).getProxy(inserter);
        // verify new Inserting
        assertEquals(proxyInserter, Whitebox.getInternalState(inserting, "inserter"));
        assertEquals(backupSeg, Whitebox.getInternalState(inserting, "seg"));
        assertEquals(1, (int) Whitebox.getInternalState(inserting, "rowCount"));
    }
}