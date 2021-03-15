package com.fr.swift.cloud.db.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.MetaDataColumnEntity;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.impl.AddColumnActionTest.SupplierResultSet;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.operator.Importer;
import com.fr.swift.cloud.source.ListBasedRow;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.cloud.source.resultset.LimitedResultSet;
import com.fr.swift.cloud.util.function.Supplier;
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
 * @date 2018/8/24
 */
@RunWith(Parameterized.class)
public class DropColumnActionTest {
    private final SwiftMetaData meta = A.getMeta();

    private boolean alterHistory;

    public DropColumnActionTest(boolean alterHistory) {
        this.alterHistory = alterHistory;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.<Object[]>asList(new Object[]{true});
    }

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.cloud.test.external.BuildCubeResource").newInstance();
    }

    @Before
    public void setUp() throws Exception {
        if (SwiftDatabase.getInstance().existsTable(new SourceKey(meta.getTableName()))) {
            SwiftDatabase.getInstance().dropTable(new SourceKey(meta.getTableName()));
        }
    }

    @Test
    public void alter() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        Table table = SwiftDatabase.getInstance().createTable(tableKey, meta);
        Importer inserter = getInserter(table, new HistoryLineSourceAlloter(tableKey, new LineAllotRule(10)));
        inserter.importResultSet(new LimitedResultSet(new SupplierResultSet(meta, new Supplier<Row>() {
            @Override
            public Row get() {
                A a = new A();
                return new ListBasedRow(Arrays.<Object>asList(a.l, a.s));
            }
        }), 25));

        new DropColumnAction(new MetaDataColumnEntity("s", Types.VARCHAR)).alter(table);

        table = SwiftDatabase.getInstance().getTable(tableKey);

        checkMeta(table);

        checkSeg(table);
    }

    @Test
    public void dropVoidColumn() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        Table table = SwiftDatabase.getInstance().createTable(tableKey, meta);
        new DropColumnAction(new MetaDataColumnEntity("voidColumn", Types.BIT)).alter(table);

        table = SwiftDatabase.getInstance().getTable(tableKey);
        Assert.assertEquals(2, table.getMeta().getColumnCount());
    }

    private Importer getInserter(Table table, HistoryLineSourceAlloter lineSourceAlloter) {
        return alterHistory ? SwiftContext.get().getBean("historyBlockInserter", Importer.class, table, lineSourceAlloter) :
                SwiftContext.get().getBean("incrementer", Importer.class, table, lineSourceAlloter);
    }

    private void checkSeg(Table table) {
        List<Segment> segs = SwiftContext.get().getBean(SegmentService.class).getSegments(table.getSourceKey());
        for (Segment seg : segs) {
            Assert.assertNull(seg.getColumn(new ColumnKey("s")));
        }
    }

    private void checkMeta(Table table) throws SwiftMetaDataException {
        SwiftMetaData meta = table.getMetadata();
        Assert.assertEquals(1, meta.getColumnCount());
        Assert.assertEquals("l", meta.getColumn(1).getName());
        Assert.assertEquals(Types.BIGINT, meta.getColumn(1).getType());
    }

    static class A {
        long l;

        String s;

        static SwiftMetaData getMeta() {
            return new SwiftMetaDataEntity("A",
                    Arrays.<SwiftMetaDataColumn>asList(
                            new MetaDataColumnEntity("l", Types.BIGINT),
                            new MetaDataColumnEntity("s", Types.VARCHAR)));
        }
    }
}