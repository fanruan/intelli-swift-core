package com.fr.swift.cloud.db;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.MetaDataColumnEntity;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.source.ListBasedRow;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
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

    @Before
    public void setUp() throws Exception {
        if (db.existsTable(sk)) {
            db.dropTable(sk);
        }

        t = db.createTable(sk, new SwiftMetaDataEntity(sk.getId(),
                Collections.<SwiftMetaDataColumn>singletonList(new MetaDataColumnEntity("A", Types.DATE))));
    }

    private void checkResult() {
        DetailColumn<Object> detailColumn = SwiftContext.get().getBean(SegmentService.class).
                getSegments(sk).get(0).getColumn(new ColumnKey("A")).getDetailColumn();
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