package com.fr.swift.data.operator.delete;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.creater.FilterCreater;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.test.Preparer;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * This class created on 2018/9/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RealtimeDecrementerTest extends TestCase {

    private SwiftSegmentManager swiftSegmentManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Preparer.prepareCubeBuild(getClass());
        swiftSegmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
    }

    public void testRealDecrementer() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testFileDeleteAndRecovery");
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource);
        incrementer.insertData(resultSet);
        assertEquals(swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey()).size(), 1);
        SegmentKey segKey = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey()).get(0);
        Segment segment = swiftSegmentManager.getSegment(segKey);
        assertTrue(segment.getLocation().getStoreType().isTransient());
        int allshowCount = 0;
        for (int i = 0; i < segment.getRowCount(); i++) {
            if (segment.getAllShowIndex().contains(i)) {
                allshowCount++;
            }
        }
        assertEquals(allshowCount, segment.getRowCount());

        Where where = new SwiftWhere(FilterCreater.createEqualFilter("合同类型", "购买合同"));
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
