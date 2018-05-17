package com.fr.swift.adaptor.log;

import com.fr.general.LogOperator;
import com.fr.swift.adaptor.log.SwiftMetaAdaptorTest.A;
import com.fr.swift.adaptor.log.SwiftMetaAdaptorTest.ConvertType;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.manager.LocalDataOperatorProvider;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        TestConfDb.setConfDb();
        SwiftContext.getInstance().registerSegmentOperatorProvider(LocalDataOperatorProvider.getInstance());
        SwiftContext.getInstance().registerSegmentProvider(LocalSegmentProvider.getInstance());
    }

    @Before
    public void setUp() throws Exception {
        if (db.existsTable(new SourceKey("A"))) {
            db.dropTable(new SourceKey("A"));
        }
        if (db.existsTable(new SourceKey("ConvertType"))) {
            db.dropTable(new SourceKey("ConvertType"));
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
        for (int i = 0; i <= SwiftLogOperator.FLUSH_SIZE_THRESHOLD; i++) {
            as.add(new A());
        }
        Thread.sleep(SwiftLogOperator.FLUSH_INTERVAL_THRESHOLD + 1);
        logOperator.recordInfo(as);
        SwiftSegmentManager segmentManager = SwiftContext.getInstance().getSegmentProvider();
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

    @Test
    public void recordInfos() {
    }
}