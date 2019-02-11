package com.fr.swift.segment.recover;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.recover.AbstractSegmentRecoveryTest.SegRecovery;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/29
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SegRecovery.class, SwiftDatabase.class})
public class AbstractSegmentRecoveryTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        when(SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(mock(SwiftSegmentManager.class));
    }

    @Test
    public void recover() throws Exception {
        SegRecovery recovery = spy(new SegRecovery());
        SourceKey tableKey = new SourceKey("t");
        List<SegmentKey> segKeys = Arrays.asList(mock(SegmentKey.class), mock(SegmentKey.class));
        doReturn(segKeys).when(recovery, "getUnstoredSegmentKeys", tableKey);

        recovery.recover(tableKey);

        verify(recovery).recover(segKeys);
    }

    @Test
    public void recoverAll() throws Exception {
        mockStatic(SwiftDatabase.class);
        when(SwiftDatabase.getInstance()).thenReturn(mock(Database.class));
        List<Table> tables = Arrays.asList(mock(Table.class), mock(Table.class));
        List<SourceKey> tableKeys = Arrays.asList(new SourceKey("t1"), new SourceKey("t2"));
        when(SwiftDatabase.getInstance().getAllTables()).thenReturn(tables);
        when(tables.get(0).getSourceKey()).thenReturn(tableKeys.get(0));
        when(tables.get(1).getSourceKey()).thenReturn(tableKeys.get(1));

        SegRecovery recovery = spy(new SegRecovery());
        doNothing().when(recovery).recover(ArgumentMatchers.<SourceKey>any());

        recovery.recoverAll();

        verify(recovery).recover(tableKeys.get(0));
        verify(recovery).recover(tableKeys.get(1));
    }

    class SegRecovery extends AbstractSegmentRecovery {

        @Override
        public void recover(List<SegmentKey> segmentKeys) {
        }
    }
}