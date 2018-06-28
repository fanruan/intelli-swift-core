package com.fr.swift.adaptor.log;

import com.fr.general.LogOperator;
import com.fr.log.message.AbstractMessage;
import com.fr.stable.query.QueryFactory;
import com.fr.swift.adaptor.log.SwiftLogOperator.Sync;
import com.fr.swift.adaptor.log.SwiftMetaAdaptorTest.A;
import com.fr.swift.adaptor.log.SwiftMetaAdaptorTest.ConvertType;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.test.Preparer;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Table;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogOperatorTest {
    private LogOperator logOperator = LogOperatorProxy.getInstance();
    private Database db = SwiftDatabase.getInstance();

    @BeforeClass
    public static void boot() throws Exception {
        Preparer.prepareCubeBuild();
        new LocalSwiftServerService().start();
        SwiftAnalyseService.getInstance().start();
    }

    @Before
    public void setUp() throws Exception {
        if (db.existsTable(new SourceKey("A"))) {
            db.dropTable(new SourceKey("A"));
        }
        if (db.existsTable(new SourceKey("ConvertType"))) {
            db.dropTable(new SourceKey("ConvertType"));
        }
        if (db.existsTable(new SourceKey("DateClass"))) {
            db.dropTable(new SourceKey("DateClass"));
        }
    }

    @Test
    public void initTables() throws Exception {
        logOperator.initTables(Arrays.asList(A.class, (Class) ConvertType.class));
        assertTrue(db.existsTable(new SourceKey("A")));
        assertTrue(db.existsTable(new SourceKey("ConvertType")));
    }

    @Test
    public void recordInfo() throws Exception {
        initTables();
        List<Object> as = new ArrayList<Object>();
        for (int i = 0; i < Sync.FLUSH_SIZE_THRESHOLD + 1; i++) {
            as.add(new A());
        }
        logOperator.recordInfo(as);
//        TimeUnit.SECONDS.sleep(40);
        SwiftSegmentManager segmentManager = SwiftContext.getInstance().getBean(SwiftSegmentManager.class);
        List<Segment> segs = segmentManager.getSegment(new SourceKey("A"));
        Segment seg = segs.get(segs.size() - 1);
        A a = (A) as.get(0);
        assertEquals((long) a.s, seg.getColumn(new ColumnKey("s")).getDetailColumn().get(0));
        assertEquals(a.l, seg.getColumn(new ColumnKey("l")).getDetailColumn().get(0));
        assertEquals(a.d1, seg.getColumn(new ColumnKey("d1")).getDetailColumn().get(0));
        assertEquals(a.d2, seg.getColumn(new ColumnKey("d2")).getDetailColumn().get(0));
        assertEquals(a.str, seg.getColumn(new ColumnKey("str")).getDetailColumn().get(0));
        assertEquals(a.utilDate.getTime(), seg.getColumn(new ColumnKey("utilDate")).getDetailColumn().get(0));
        assertEquals(a.sqlDate.getTime(), seg.getColumn(new ColumnKey("sqlDate")).getDetailColumn().get(0));
        assertEquals((long) a.i, seg.getColumn(new ColumnKey("i")).getDetailColumn().get(0));
        assertEquals(a.b ? 1L : 0L, seg.getColumn(new ColumnKey("b")).getDetailColumn().get(0));
    }

    @Table(name = "DateClass")
    static class DateClass {
        @Column(name = AbstractMessage.COLUMN_TIME)
        Date time;

        public DateClass() {
        }

        public DateClass(long t) {
            this.time = new Date(t);
        }
    }

    @Test
    public void clearLogBefore() throws Exception {
        logOperator.initTables(Collections.<Class>singletonList(DateClass.class));
        long mid = -1;

        List<Object> dates = new ArrayList<Object>();
        for (int i = 0; i < Sync.FLUSH_SIZE_THRESHOLD + 1; i++) {
            if (i == Sync.FLUSH_SIZE_THRESHOLD / 2) {
                mid = i;
            }
            dates.add(new DateClass(i));
        }
        logOperator.recordInfo(dates);
        TimeUnit.SECONDS.sleep(1);
        logOperator.clearLogBefore(new Date(mid));
        TimeUnit.SECONDS.sleep(1);
        List<DateClass> data = logOperator.find(DateClass.class, QueryFactory.create()).getList();
        for (DateClass datum : data) {
            Assert.assertTrue(datum.time.compareTo(new Date(mid)) > 0);
        }

    }
}