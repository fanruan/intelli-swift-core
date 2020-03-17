package com.fr.swift.segment.recover;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/1/29
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, AbstractSegmentRecovery.class, FileSegmentRecovery.class, SwiftEventDispatcher.class, SwiftInserter.class})
public class FileSegmentRecoveryTest {

    @Test
    public void recover() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        SegmentService segmentManager = mock(SegmentService.class);
        when(SwiftContext.get().getBean(SegmentService.class)).thenReturn(segmentManager);

        Segment realtimeSeg = mock(Segment.class);
        SegmentKey segKey = mock(SegmentKey.class);
        when(segmentManager.getSegment(segKey)).thenReturn(realtimeSeg);

        FileSegmentRecovery recovery = spy(new FileSegmentRecovery());
        doReturn(realtimeSeg).when(recovery, "newRealtimeSegment", realtimeSeg);

        SwiftInserter inserter = mock(SwiftInserter.class);
        mockStatic(SwiftInserter.class);
        when(SwiftInserter.ofOverwriteMode(realtimeSeg)).thenReturn(inserter);

        Segment backupSeg = mock(Segment.class);
        SwiftMetaData meta = mock(SwiftMetaData.class);
        when(realtimeSeg.getMetaData()).thenReturn(meta);
        doReturn(backupSeg).when(recovery, "getBackupSegment", segKey, meta);

        SegmentBackupResultSet resultSet = mock(SegmentBackupResultSet.class);
        whenNew(SegmentBackupResultSet.class).withArguments(backupSeg).thenReturn(resultSet);

        doNothing().doThrow(Exception.class).when(inserter).insertData(resultSet);
        when(resultSet.getAllShowIndex()).thenReturn(mock(ImmutableBitMap.class));

//        when(realtimeSeg.getRowCount()).thenReturn(BaseAllotRule.MEM_CAPACITY);
//        mockStatic(SwiftEventDispatcher.class);

        Whitebox.invokeMethod(recovery, "recover", segKey);

        verify(inserter).insertData(resultSet);

        verify(realtimeSeg).putAllShowIndex(resultSet.getAllShowIndex());

//        verifyStatic(SwiftEventDispatcher.class);
//        SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);

        Whitebox.invokeMethod(recovery, "recover", segKey);

        // insert失败
        verify(realtimeSeg).putRowCount(0);
        verify(realtimeSeg).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap argument) {
                return argument.isFull() && argument.isEmpty();
            }
        }));
    }
}