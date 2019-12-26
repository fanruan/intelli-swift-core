package com.fr.swift.segment.operator.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Database;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.insert.BaseBlockImporter.Inserting;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/12
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SwiftDatabase.class, SwiftEventDispatcher.class, SegmentUtils.class})
public class BaseBlockImporterTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class, SwiftEventDispatcher.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        mockStatic(SwiftDatabase.class);
        when(SwiftDatabase.getInstance()).thenReturn(mock(Database.class));
    }

    @Test
    public void importData() throws Exception {
        SwiftTableAllotRuleService allotRuleService = mock(SwiftTableAllotRuleService.class);
        when(SwiftContext.get().getBean(SwiftTableAllotRuleService.class)).thenReturn(allotRuleService);

        DataSource dataSource = mock(DataSource.class, RETURNS_DEEP_STUBS);
        SwiftSourceAlloter alloter = mock(SwiftSourceAlloter.class);

        SwiftResultSet resultSet = mock(SwiftResultSet.class);
        when(resultSet.hasNext()).thenReturn(true, true, true, false);
        when(resultSet.getNextRow()).thenReturn(new ListBasedRow(0), new ListBasedRow(1), new ListBasedRow(2)).thenThrow(SQLException.class);

        when(alloter.getAllotRule()).thenReturn(new LineAllotRule(2));
        SwiftSegmentInfo segInfo0 = new SwiftSegmentInfo(0, StoreType.FINE_IO), segInfo1 = new SwiftSegmentInfo(1, StoreType.FINE_IO);
        when(alloter.allot(ArgumentMatchers.<RowInfo>any())).thenReturn(segInfo0, segInfo0, segInfo1);

        when(dataSource.getSourceKey()).thenReturn(mock(SourceKey.class));
        when(dataSource.getMetadata().getSwiftSchema()).thenReturn(SwiftSchema.CUBE);

        BaseBlockImporter<?, SwiftResultSet> blockImporter = spy(new BlockImporter<SwiftSourceAlloter<?, RowInfo>>(dataSource, alloter));

        Inserting inserting0 = mock(Inserting.class), inserting1 = mock(Inserting.class);
        when(blockImporter, "getInserting", any()).thenReturn(inserting0, inserting1);

        when(inserting0.isFull()).thenReturn(true);

        blockImporter.importData(resultSet);

        //verify persist meta
        verify(SwiftDatabase.getInstance()).existsTable(dataSource.getSourceKey());
        verify(SwiftDatabase.getInstance()).createTable(dataSource.getSourceKey(), dataSource.getMetadata());
        verify(allotRuleService).getAllotRuleByTable(dataSource.getSourceKey());
        verify(allotRuleService).saveAllotRule(Mockito.any(SwiftTableAllotRule.class));

        verify(resultSet, times(4)).hasNext();
        verify(resultSet, times(3)).getNextRow();
        // releaseFullIfExists
        verify(inserting0).release();
        verify(inserting1).release();

        verify(blockImporter).indexIfNeed(segInfo0);
        verify(blockImporter).indexIfNeed(segInfo1);

        verify(blockImporter).handleFullSegment(segInfo0);

        verify(inserting0, times(2)).insert(ArgumentMatchers.<Row>any());
        verify(inserting1).insert(ArgumentMatchers.<Row>any());

        // exception
        when(resultSet.hasNext()).thenThrow(new SQLException());
        try {
            blockImporter.importData(resultSet);
            fail();
        } catch (Exception e) {
            verify(blockImporter).onFailed();
        }

        // finally
        verify(blockImporter, times(2)).processAfterSegmentDone(anyBoolean());
        verify(resultSet, times(2)).close();
        verify(blockImporter, times(2)).release();
    }

    @Test
    public void getFields() {
        DataSource dataSource = mock(DataSource.class, RETURNS_DEEP_STUBS);
        List fields = mock(List.class);
        when(dataSource.getMetadata().getFieldNames()).thenReturn(fields);

        assertEquals(fields, new BlockImporter(dataSource, mock(SwiftSourceAlloter.class)).getFields());
    }

    @Test
    public void getImportSegments() {
        DataSource dataSource = mock(DataSource.class);

        BlockImporter blockImporter = new BlockImporter(dataSource, mock(SwiftSourceAlloter.class));
        List segKeys = mock(List.class);
        Whitebox.setInternalState(blockImporter, "importSegKeys", segKeys);

        assertEquals(segKeys, blockImporter.getImportSegments());
    }

    static class BlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, SwiftResultSet> {

        public BlockImporter(DataSource dataSource, A alloter) {
            super(dataSource, alloter);
        }

        @Override
        protected Inserting getInserting(SegmentKey segKey) {
            return null;
        }

        @Override
        protected void handleFullSegment(SegmentInfo segInfo) {
        }

        @Override
        protected void onSucceed() {

        }

        @Override
        protected void onFailed() {
        }
    }
}