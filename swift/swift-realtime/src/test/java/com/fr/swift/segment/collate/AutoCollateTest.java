package com.fr.swift.segment.collate;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.SwiftCollateService;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.task.service.SwiftServiceTaskExecutor;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2018/9/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AutoCollateTest extends TestCase {

    private SwiftSegmentManager swiftSegmentManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        swiftSegmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        SwiftContext.get().getBean("localManager", ServiceManager.class).startUp();
    }

    @Test
    public void testAppointRealtimeCollate() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "testAppointRealtimeCollate");
        SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).removeSegments(dataSource.getSourceKey().getId());
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Incrementer incrementer = new Incrementer(dataSource, new LineSourceAlloter(dataSource.getSourceKey(), new LineAllotRule(100)));
        incrementer.insertData(resultSet);

        List<SegmentKey> segKeys = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());

        Where where = new SwiftWhere(createEqualFilter("合同类型", "购买合同"));
        //合并前7块增量块，且只要allshow是购买合同
        assertEquals(7, segKeys.size());
        for (SegmentKey segKey : segKeys) {
            Segment segment = swiftSegmentManager.getSegment(segKey);
            Decrementer decrementer = new Decrementer(segKey);
            decrementer.delete(where);
            assertSame(segment.getLocation().getStoreType(), Types.StoreType.MEMORY);
            Column column = segment.getColumn(new ColumnKey("合同类型"));
            int neqCount = 0;
            for (int i = 0; i < segment.getRowCount(); i++) {
                if (segment.getAllShowIndex().contains(i)) {
                    Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
                } else {
                    neqCount++;
                }
            }
            assertTrue(neqCount != 0);
        }

        List<SegmentKey> segmentKeyList = swiftSegmentManager.getSegmentKeys(dataSource.getSourceKey());
        assertEquals(7, segmentKeyList.size());
        //取4个进行合并
        List<SegmentKey> collateSegmentKeys = new ArrayList<SegmentKey>();
        collateSegmentKeys.add(segmentKeyList.get(0));
        collateSegmentKeys.add(segmentKeyList.get(1));
        collateSegmentKeys.add(segmentKeyList.get(2));
        collateSegmentKeys.add(segmentKeyList.get(3));


        //合并增量块，直接写history
        SwiftCollateService collaterService = SwiftContext.get().getBean(SwiftCollateService.class);
        collaterService.setTaskExecutor(new SwiftServiceTaskExecutor());
        collaterService.appointCollate(dataSource.getSourceKey(), collateSegmentKeys);

        Thread.sleep(5000L);
        List<Segment> segments = swiftSegmentManager.getSegment(dataSource.getSourceKey());
        assertEquals(4, segments.size());
        //合并后1块历史块,3块增量块，历史块所有数据都不是购买合同，增量快allshow不是购买合同
        int hisCount = 0;
        int realCount = 0;
        for (Segment segment : segments) {
            if (segment.getLocation().getStoreType().isPersistent()) {
                Column column = segment.getColumn(new ColumnKey("合同类型"));
                for (int i = 0; i < segment.getRowCount(); i++) {
                    Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
                }
                hisCount++;
            } else {
                Column column = segment.getColumn(new ColumnKey("合同类型"));
                int neqCount = 0;
                for (int i = 0; i < segment.getRowCount(); i++) {
                    if (segment.getAllShowIndex().contains(i)) {
                        Assert.assertNotEquals(column.getDetailColumn().get(i), "购买合同");
                    } else {
                        neqCount++;
                    }
                }
                assertTrue(neqCount != 0);
                realCount++;
            }
        }
        assertEquals(1, hisCount);
        assertEquals(3, realCount);
    }

    static FilterBean createEqualFilter(String fieldName, String value) {
        InFilterBean bean = new InFilterBean();
        bean.setColumn(fieldName);
        bean.setFilterValue(Collections.singleton(value));
        return bean;
    }
}
