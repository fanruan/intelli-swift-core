package com.fr.swift.db.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Schema;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.test.Preparer;
import com.fr.swift.util.JpaAdaptor;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/23
 */
@RunWith(Parameterized.class)
public class SegmentTransferTest {

    private final SwiftMetaData meta = JpaAdaptor.adapt(A.class);
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

    @Before
    public void setUp() throws Exception {
        Preparer.prepareCubeBuild(getClass());
        SourceKey tableKey = new SourceKey(meta.getTableName());
        if (SwiftDatabase.getInstance().existsTable(tableKey)) {
            SwiftDatabase.getInstance().dropTable(tableKey);
        }
    }

    @Test
    public void transfer() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        SwiftDatabase.getInstance().createTable(tableKey, meta);

        SegmentKey oldSegKey = new SegmentKeyBean(tableKey.getId(), 0, from, Schema.CUBE),
                newSegKey = new SegmentKeyBean(tableKey.getId(), 0, to, Schema.CUBE);

        Segment oldSeg = SegmentUtils.newSegment(oldSegKey);
        Inserter inserter = new SwiftInserter(oldSeg);
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
            return new ListBasedRow(Arrays.asList(a.l, a.d, a.s));
        }

        @Override
        public void close() {
        }
    }

    @Entity
    @Table(name = "A")
    class A {
        @Column(name = "l")
        long l = 2;

        @Column(name = "d")
        double d = 0.76;

        @Column(name = "s")
        String s = "string";
    }
}