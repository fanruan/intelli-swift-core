package com.fr.swift.db;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;

import java.sql.Types;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/4/9
 */
@FixMethodOrder(MethodSorters.JVM)
public class TableTest {
    private Database db = SwiftDatabase.getInstance();
    private SourceKey sk = new SourceKey(getClass().getSimpleName());
    private Table t;

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Before
    public void setUp() throws Exception {
        if (db.existsTable(sk)) {
            db.dropTable(sk);
        }

        t = db.createTable(sk, new SwiftMetaDataBean(sk.getId(),
                Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumnBean("A", Types.DATE))));
    }

    private void checkResult() {
        DetailColumn<Object> detailColumn = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).
                getSegment(sk).get(0).getColumn(new ColumnKey("A")).getDetailColumn();
        assertEquals(2L, detailColumn.get(0));
        assertEquals(3L, detailColumn.get(1));
        assertEquals(45L, detailColumn.get(2));
        assertEquals(8L, detailColumn.get(3));
        assertEquals(234L, detailColumn.get(4));
    }

    @After
    public void tearDown() throws Exception {
        if (db.existsTable(sk)) {
            db.dropTable(sk);
        }
    }

    static class RowSet implements SwiftResultSet {
        Row[] rows = {
                new ListBasedRow(Collections.<Object>singletonList(2L)),
                new ListBasedRow(Collections.<Object>singletonList(3L)),
                new ListBasedRow(Collections.<Object>singletonList(45L)),
                new ListBasedRow(Collections.<Object>singletonList(8L)),
                new ListBasedRow(Collections.<Object>singletonList(234L))
        };
        int cursor = 0;

        @Override
        public void close() {
            rows = null;
        }

        @Override
        public boolean hasNext() {
            return cursor < rows.length;
        }

        @Override
        public int getFetchSize() {
            return 0;
        }

        @Override
        public SwiftMetaData getMetaData() {
            return null;
        }

        @Override
        public Row getNextRow() {
            return rows[cursor++];
        }
    }
}