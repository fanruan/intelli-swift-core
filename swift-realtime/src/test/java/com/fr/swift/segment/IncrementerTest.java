package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.backup.SwiftSegmentBackup;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.transaction.TransactionProxyFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
@PrepareForTest({SwiftContext.class, SwiftDatabase.class, SwiftEventDispatcher.class, Incrementer.class})
public class IncrementerTest {

    @Mock
    private Database database;
    @Mock
    private SwiftRealtimeInserter inserter;
    @Mock
    private TransactionProxyFactory transactionProxyFactory;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);

        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);
        SwiftMetaDataService metaDataService = mock(SwiftMetaDataService.class);
        when(beanFactory.getBean(SwiftMetaDataService.class)).thenReturn(metaDataService);

        SwiftCubePathService swiftCubePathService = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(swiftCubePathService);
        when(swiftCubePathService.getSwiftPath()).thenReturn("/");

        mockStatic(SwiftDatabase.class);
        when(SwiftDatabase.getInstance()).thenReturn(database);

        mockStatic(SwiftEventDispatcher.class);

        whenNew(SwiftRealtimeInserter.class).withAnyArguments().thenReturn(inserter);
        SwiftSegmentBackup segmentBackup = mock(SwiftSegmentBackup.class);
        when(inserter.getSwiftBackup()).thenReturn(segmentBackup);
        whenNew(TransactionProxyFactory.class).withAnyArguments().thenReturn(transactionProxyFactory);
        when(transactionProxyFactory.getProxy(inserter)).thenReturn(inserter);
    }

    @Test
    public void importData() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        SourceKey tableKey = new SourceKey("tbl");
        when(dataSource.getSourceKey()).thenReturn(tableKey);
        SwiftMetaData metaData = mock(SwiftMetaData.class);
        when(dataSource.getMetadata()).thenReturn(metaData);
        when(metaData.getSwiftDatabase()).thenReturn(com.fr.swift.db.SwiftDatabase.CUBE);

        SwiftSourceAlloter alloter = mock(SwiftSourceAlloter.class);
        when(alloter.getAllotRule()).thenReturn(new LineAllotRule(1));
        when(alloter.allot(ArgumentMatchers.<RowInfo>any())).thenReturn(new SwiftSegmentInfo(0, StoreType.MEMORY),
                new SwiftSegmentInfo(1, StoreType.MEMORY),
                new SwiftSegmentInfo(2, StoreType.MEMORY));

        Incrementer<?> incrementer = spy(new Incrementer<SwiftSourceAlloter<AllotRule, RowInfo>>(dataSource, alloter));
        Segment seg = mock(Segment.class);
        doReturn(seg).when(incrementer).newSegment(ArgumentMatchers.<SegmentKey>any());

        when(seg.isReadable()).thenReturn(true);
        when(seg.getRowCount()).thenReturn(1);

        SwiftResultSet resultSet = mock(SwiftResultSet.class);
        when(resultSet.hasNext()).thenReturn(true, true, true, false);
        when(resultSet.getNextRow()).thenReturn(new ListBasedRow(1), new ListBasedRow(2), new ListBasedRow(3));

        incrementer.importData(resultSet);

        // create if not exists
        verify(database).existsTable(tableKey);
        verify(database).createTable(tableKey, metaData);
        // allot and insert
        verify(alloter, times(3)).allot(ArgumentMatchers.<RowInfo>any());
        // transaction proxy
        verify(transactionProxyFactory, times(3)).getProxy(inserter);
        verify(inserter, times(3)).insertData(ArgumentMatchers.<Row>any());
        // ensure close and release
        verify(resultSet).close();
        verify(inserter, times(3)).release();
        // ensure transfer full real time seg
        for (SegmentKey segKey : incrementer.getImportSegments()) {
            verifyStatic(SwiftEventDispatcher.class);
            SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);
        }
        // ensure sync all seg location
        verifyStatic(SwiftEventDispatcher.class);
        SwiftEventDispatcher.fire(SyncSegmentLocationEvent.PUSH_SEG, incrementer.getImportSegments());
    }

    @Test
    public void getFields() {
        DataSource dataSource = mock(DataSource.class);
        SwiftMetaData metaData = mock(SwiftMetaData.class);
        when(dataSource.getMetadata()).thenReturn(metaData);
        List<String> list = mock(List.class);
        when(metaData.getFieldNames()).thenReturn(list);

        List<String> fields = new Incrementer<SwiftSourceAlloter<AllotRule, RowInfo>>(dataSource, mock(SwiftSourceAlloter.class)).getFields();
        assertEquals(list, fields);
    }
}