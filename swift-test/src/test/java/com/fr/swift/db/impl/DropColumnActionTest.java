package com.fr.swift.db.impl;

import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.impl.AddColumnActionTest.SupplierResultSet;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.insert.HistoryBlockInserter;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.test.Preparer;
import com.fr.swift.util.JpaAdaptor;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Table;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
    private final SwiftMetaData meta = JpaAdaptor.adapt(A.class);

    private boolean alterHistory;

    public DropColumnActionTest(boolean alterHistory) {
        this.alterHistory = alterHistory;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.<Object[]>asList(new Object[]{true});
    }

    @Before
    public void setUp() throws Exception {
        Preparer.prepareCubeBuild(getClass());
        if (SwiftDatabase.getInstance().existsTable(new SourceKey(meta.getTableName()))) {
            SwiftDatabase.getInstance().dropTable(new SourceKey(meta.getTableName()));
        }
    }

    @Test
    public void alter() throws Exception {
        SourceKey tableKey = new SourceKey(meta.getTableName());
        com.fr.swift.db.Table table = SwiftDatabase.getInstance().createTable(tableKey, meta);
        Inserter inserter = getInserter(table, new LineSourceAlloter(tableKey, new LineAllotRule(10)));
        inserter.insertData(new LimitedResultSet(new SupplierResultSet(meta, () -> {
            A a = new A();
            return new ListBasedRow(Arrays.asList(a.l, a.s));
        }), 25));

        new DropColumnAction(new MetaDataColumnBean("s", Types.VARCHAR)).alter(table);

        table = SwiftDatabase.getInstance().getTable(tableKey);

        checkMeta(table);

        checkSeg(table);
    }

    private Inserter getInserter(com.fr.swift.db.Table table, LineSourceAlloter lineSourceAlloter) {
        return alterHistory ? new HistoryBlockInserter(table, lineSourceAlloter) :
                new Incrementer(table, lineSourceAlloter);
    }

    private void checkSeg(com.fr.swift.db.Table table) {
        List<Segment> segs = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(table.getSourceKey());
        for (Segment seg : segs) {
            Assert.assertNull(seg.getColumn(new ColumnKey("s")));
        }
    }

    private void checkMeta(com.fr.swift.db.Table table) throws SwiftMetaDataException {
        SwiftMetaData meta = table.getMetadata();
        Assert.assertEquals(1, meta.getColumnCount());
        Assert.assertEquals("l", meta.getColumn(1).getName());
        Assert.assertEquals(Types.BIGINT, meta.getColumn(1).getType());
    }

    @Table(name = "A")
    class A {
        @Column(name = "l")
        long l;

        @Column(name = "s")
        String s;
    }
}