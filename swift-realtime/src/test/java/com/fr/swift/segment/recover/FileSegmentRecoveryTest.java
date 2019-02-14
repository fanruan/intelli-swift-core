package com.fr.swift.segment.recover;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
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
@PrepareForTest({SwiftContext.class, AbstractSegmentRecovery.class, FileSegmentRecovery.class})
public class FileSegmentRecoveryTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        when(SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(mock(SwiftSegmentManager.class));
    }

    @Test
    public void recover() throws Exception {
        FileSegmentRecovery recovery = spy(new FileSegmentRecovery());

        Segment realtimeSeg = mock(Segment.class), realtimeSeg1 = mock(Segment.class);
        doReturn(realtimeSeg, realtimeSeg1).when(recovery, "newRealtimeSegment", ArgumentMatchers.<Segment>any());

        doReturn(mock(Segment.class)).when(recovery, "getBackupSegment", ArgumentMatchers.<SegmentKey>any(), ArgumentMatchers.<SwiftMetaData>any());

        SwiftInserter inserter = mock(SwiftInserter.class), inserter1 = mock(SwiftInserter.class);
        whenNew(SwiftInserter.class).withAnyArguments().thenReturn(inserter, inserter1);
        doThrow(new Exception()).when(inserter1).insertData(ArgumentMatchers.<SwiftResultSet>any());

        SegmentBackupResultSet resultSet = mock(SegmentBackupResultSet.class), resultSet1 = mock(SegmentBackupResultSet.class);
        when(resultSet.getAllShowIndex()).thenReturn(mock(ImmutableBitMap.class));
        when(resultSet1.getAllShowIndex()).thenReturn(mock(ImmutableBitMap.class));

        whenNew(SegmentBackupResultSet.class).withAnyArguments().thenReturn(resultSet, resultSet1);

        recovery.recover(Arrays.asList(mock(SegmentKey.class), mock(SegmentKey.class)));

        verify(inserter).insertData(resultSet);
        verify(inserter1).insertData(resultSet1);

        verify(realtimeSeg).putAllShowIndex(resultSet.getAllShowIndex());

        // insert失败
        verify(realtimeSeg1).putRowCount(0);
        verify(realtimeSeg1).putAllShowIndex(argThat(new ArgumentMatcher<ImmutableBitMap>() {
            @Override
            public boolean matches(ImmutableBitMap argument) {
                return argument.isFull() && argument.isEmpty();
            }
        }));
    }
}