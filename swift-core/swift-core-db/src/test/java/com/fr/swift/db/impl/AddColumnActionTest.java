package com.fr.swift.db.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/24
 */
@Ignore
@RunWith(Parameterized.class)
public class AddColumnActionTest {
    private final SwiftMetaData meta = A.getMeta();

    private boolean alterHistory;

    public AddColumnActionTest(boolean alterHistory) {
        this.alterHistory = alterHistory;
    }

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Before
    public void setUp() throws Exception {
        if (SwiftDatabase.getInstance().existsTable(new SourceKey(meta.getTableName()))) {
            SwiftDatabase.getInstance().dropTable(new SourceKey(meta.getTableName()));
        }
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.<Object[]>asList(/*new Object[]{true},*/ new Object[]{false});
    }

    @Test
    public void alter() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        com.fr.swift.db.Table table = SwiftDatabase.getInstance().createTable(tableKey, meta);
        Importer inserter = getInserter(table, new HistoryLineSourceAlloter(tableKey, new LineAllotRule(10)));
        inserter.importData(new LimitedResultSet(new SupplierResultSet(meta, new Supplier<Row>() {
            @Override
            public Row get() {
                A a = new A();
                return new ListBasedRow(Collections.<Object>singletonList(a.i));
            }
        }), 25));

        new AddColumnAction(new MetaDataColumnBean("d", Types.DOUBLE)).alter(table);

        table = SwiftDatabase.getInstance().getTable(tableKey);

        checkMeta(table);

        checkSeg(table);
    }

    @Test
    public void addPresentedColumn() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        com.fr.swift.db.Table table = SwiftDatabase.getInstance().createTable(tableKey, meta);
        new AddColumnAction(new MetaDataColumnBean("i", Types.VARCHAR)).alter(table);

        table = SwiftDatabase.getInstance().getTable(tableKey);
        Assert.assertEquals(1, table.getMeta().getColumnCount());
    }

    private Importer getInserter(com.fr.swift.db.Table table, HistoryLineSourceAlloter lineSourceAlloter) {
        return alterHistory ? SwiftContext.get().getBean("historyBlockInserter", Importer.class, table, lineSourceAlloter) :
                SwiftContext.get().getBean("incrementer", Importer.class, table, lineSourceAlloter);
    }

    private void checkSeg(com.fr.swift.db.Table table) {
        List<Segment> segs = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(table.getSourceKey());
        for (Segment seg : segs) {
            int rowCount = seg.getRowCount();
            com.fr.swift.segment.column.Column<Object> column = seg.getColumn(new ColumnKey("d"));
            DetailColumn<Object> detail = column.getDetailColumn();
            DictionaryEncodedColumn<Object> dict = column.getDictionaryEncodedColumn();
            for (int i = 0; i < rowCount; i++) {
                Assert.assertNull(detail.get(i));
                Assert.assertEquals(0, dict.getIndexByRow(i));
                Assert.assertEquals(0, dict.getGlobalIndexByRow(i));
                Assert.assertNull(dict.getValueByRow(i));
            }

            Assert.assertEquals(1, dict.size());
            Assert.assertEquals(1, dict.globalSize());
            Assert.assertNull(dict.getValue(0));
            Assert.assertEquals(0, dict.getIndex(null));
        }
    }

    private void checkMeta(com.fr.swift.db.Table table) throws SwiftMetaDataException {
        SwiftMetaData meta = table.getMetadata();
        Assert.assertEquals(2, meta.getColumnCount());
        Assert.assertEquals("d", meta.getColumn(2).getName());
        Assert.assertEquals(Types.DOUBLE, meta.getColumn(2).getType());
    }

    static class SupplierResultSet implements SwiftResultSet {
        SwiftMetaData meta;
        Supplier<Row> rows;

        public SupplierResultSet(SwiftMetaData meta, Supplier<Row> rows) {
            this.meta = meta;
            this.rows = rows;
        }

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
            return rows.get();
        }

        @Override
        public void close() {
        }
    }

    static class A {
        long i = -1;

        static SwiftMetaData getMeta() {
            return new SwiftMetaDataBean("A",
                    Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumnBean("i", Types.BIGINT)));
        }
    }
}