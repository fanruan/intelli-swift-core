package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.LimitedResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/23
 */
@RunWith(Parameterized.class)
public class SegmentTransferTest {

    private final SwiftMetaData meta = A.getMeta();
    private final A a = new A();

    private StoreType from, to;

    public SegmentTransferTest(StoreType from, StoreType to) {
        this.from = from;
        this.to = to;
    }

    @Parameters
    public static Collection<StoreType[]> data() {
        return Arrays.<StoreType[]>asList(
                new StoreType[]{StoreType.MEMORY, StoreType.FINE_IO});
    }

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Before
    public void setUp() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        if (com.fr.swift.db.impl.SwiftDatabase.getInstance().existsTable(tableKey)) {
            com.fr.swift.db.impl.SwiftDatabase.getInstance().dropTable(tableKey);
        }
    }

    @Rule
    public TestRule getReleasableLeakVerifier() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (TestRule) Class.forName("com.fr.swift.test.ReleasableLeakVerifier").newInstance();
    }

    @Test
    public void transfer() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        com.fr.swift.db.impl.SwiftDatabase.getInstance().createTable(tableKey, meta);

        SegmentKey oldSegKey = new SegmentKeyBean(tableKey, 0, from, SwiftDatabase.CUBE),
                newSegKey = new SegmentKeyBean(tableKey, 0, to, SwiftDatabase.CUBE);

        Segment oldSeg = SegmentUtils.newSegment(oldSegKey);
        Inserter inserter = (Inserter) SwiftContext.get().getBean("inserter", oldSeg);
        int rowCount = 100;
        inserter.insertData(new LimitedResultSet(new ResultSet(), rowCount));

        assertSegUsable(rowCount, oldSegKey);

        new SegmentTransfer(oldSegKey, newSegKey).transfer();

        assertSegUsable(rowCount, newSegKey);

        assertConfUpdated(tableKey);

        assertOldSegRemoved(oldSegKey);
    }

    private void assertOldSegRemoved(SegmentKey oldSegKey) {
        Segment oldSeg = SegmentUtils.newSegment(oldSegKey);
        Assert.assertFalse(oldSeg.isReadable());
        com.fr.swift.segment.column.Column<Object> column = oldSeg.getColumn(new ColumnKey("s"));
        Assert.assertFalse(column.getDetailColumn().isReadable());
        Assert.assertFalse(column.getDictionaryEncodedColumn().isReadable());
    }

    private void assertConfUpdated(SourceKey tableKey) {
        List<SegmentKey> segKeys = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegmentKeys(tableKey);
        Assert.assertEquals(1, segKeys.size());
        Assert.assertEquals(to, segKeys.get(0).getStoreType());
    }

    private void assertSegUsable(int rowCount, SegmentKey segKey) {
        Segment seg = SegmentUtils.newSegment(segKey);
        Assert.assertEquals(rowCount, seg.getRowCount());

        com.fr.swift.segment.column.Column<Object> column = seg.getColumn(new ColumnKey("l"));
        DetailColumn<Object> detail = column.getDetailColumn();

        for (int i = 0; i < rowCount; i++) {
            Assert.assertEquals(a.l, detail.get(i));
        }

        if (!seg.isHistory()) {
            DictionaryEncodedColumn<Object> dict = column.getDictionaryEncodedColumn();
            Assert.assertEquals(2, dict.size());
            for (int i = 0; i < rowCount; i++) {
                Assert.assertEquals(a.l, dict.getValue(dict.getIndexByRow(i)));
            }
        }
    }

    class ResultSet implements SwiftResultSet {
        @Override
        public int getFetchSize() {
            return 0;
        }

        @Override
        public SwiftMetaData getMetaData() {
            return meta;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Row getNextRow() {
            return new ListBasedRow(Arrays.<Object>asList(a.l, a.d, a.s));
        }

        @Override
        public void close() {
        }
    }

    static class A {
        long l = 2;

        double d = 0.76;

        String s = "string";

        static SwiftMetaData getMeta() {
            return new SwiftMetaDataBean("A",
                    Arrays.<SwiftMetaDataColumn>asList(
                            new MetaDataColumnBean("l", Types.BIGINT),
                            new MetaDataColumnBean("d", Types.DOUBLE),
                            new MetaDataColumnBean("s", Types.VARCHAR)));
        }
    }
}