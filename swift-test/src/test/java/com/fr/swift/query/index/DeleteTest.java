package com.fr.swift.query.index;

import com.fr.stable.query.QueryFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.RestrictionFactory;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftRealtimeService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * This class created on 2018/4/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class DeleteTest extends BaseTest {

    private final Database db = SwiftDatabase.getInstance();

    private LocalSegmentProvider localSegmentProvider = (LocalSegmentProvider) SwiftContext.getInstance().getBean("localSegmentProvider");

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        SwiftAnalyseService.getInstance().start();
    }

    @Test
    public void testEQHis() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testEQHis");
            if (!db.existsTable(new SourceKey("testEQHis"))) {
                db.createTable(new SourceKey("testEQHis"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("testEQHis"));
            transportHisAndIndex(dataSource, table);
            QueryCondition eqQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
            Where where = new SwiftWhere(eqQueryCondition);
            boolean result = SwiftHistoryService.getInstance().delete(new SourceKey("testEQHis"), where);
            assertTrue(result);
            Segment segment = localSegmentProvider.getSegment(new SourceKey("testEQHis")).get(0);
            Column column = segment.getColumn(new ColumnKey("合同类型"));
            for (int i = 0; i < segment.getRowCount(); i++) {
                if (segment.getAllShowIndex().contains(i)) {
                    assertEquals(column.getDetailColumn().get(i), "购买合同");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

    @Test
    public void testEQReal() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testEQReal");
            if (!db.existsTable(new SourceKey("testEQReal"))) {
                db.createTable(new SourceKey("testEQReal"), dataSource.getMetadata());
            }
            Table table = db.getTable(new SourceKey("testEQReal"));
            transportRealAndIndex(dataSource, table);
            QueryCondition eqQueryCondition = QueryFactory.create().addRestriction(RestrictionFactory.eq("合同类型", "购买合同"));
            Where where = new SwiftWhere(eqQueryCondition);
            boolean result = SwiftRealtimeService.getInstance().delete(new SourceKey("testEQReal"), where);
            assertTrue(result);
            Segment segment = localSegmentProvider.getSegment(new SourceKey("testEQReal")).get(0);
            Column column = segment.getColumn(new ColumnKey("合同类型"));
            for (int i = 0; i < segment.getRowCount(); i++) {
                if (segment.getAllShowIndex().contains(i)) {
                    assertEquals(column.getDetailColumn().get(i), "购买合同");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

    protected void transportHisAndIndex(DataSource dataSource, Table table) throws Exception {
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        table.importFrom(resultSet);
    }

    protected void transportRealAndIndex(DataSource dataSource, Table table) throws Exception {
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        table.insert(resultSet);
    }
}
