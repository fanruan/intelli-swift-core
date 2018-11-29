package com.fr.swift.segment.decrement;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.generate.ColumnIndexer;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.insert.HistoryBlockInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Ignore;

/**
 * This class created on 2018/9/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Ignore
public class HistoryDecrementerTest extends TestCase {

    private SwiftSegmentManager swiftSegmentManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        swiftSegmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
    }

    public void testHisDecrementer() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testFileDeleteAndRecovery");
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        HistoryBlockInserter inserter = new HistoryBlockInserter(dataSource);
        inserter.insertData(resultSet);

        for (String fieldName : dataSource.getMetadata().getFieldNames()) {
            ColumnIndexer columnIndexer = new ColumnIndexer(dataSource, new ColumnKey(fieldName), swiftSegmentManager.getSegment(dataSource.getSourceKey()));
            columnIndexer.work();
        }
        assertEquals(swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey()).size(), 1);
        SegmentKey segKey = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey()).get(0);
        Segment segment = swiftSegmentManager.getSegment(segKey);
        assertTrue(segment.getLocation().getStoreType().isPersistent());
        int allshowCount = 0;
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                allshowCount++;
            }
        }
        assertEquals(allshowCount, segment.getRowCount());

        Where where = null;
//        Where where = new SwiftWhere(FilterCreater.createEqualFilter("合同类型", "购买合同"));
        Decrementer decrementer = new Decrementer(segKey);
        decrementer.delete(where);
        //增量删除后内存数据判断
        Column column = segment.getColumn(new ColumnKey("合同类型"));
        allshowCount = 0;
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
                allshowCount++;
            }
        }
        assertNotSame(allshowCount, segment.getRowCount());
    }
}
