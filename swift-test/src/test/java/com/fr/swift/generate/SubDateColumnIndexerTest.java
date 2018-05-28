package com.fr.swift.generate;

import com.fr.swift.config.TestConfDb;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.index.SubDateColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.DateColumn;
import com.fr.swift.segment.column.impl.DateDerivers;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.util.function.Function;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author anchore
 * @date 2018/3/24
 */
public class SubDateColumnIndexerTest {
    private final LocalSegmentProvider segmentProvider = SwiftContext.getInstance().getBean(LocalSegmentProvider.class);
    private String columnName = "注册时间";
    private DataSource dataSource = new QueryDBSource("select " + columnName + " from DEMO_CONTRACT", getClass().getName());

    @BeforeClass
    public static void beforeClass() throws Exception {
        new LocalSwiftServerService().start();
        TestConfDb.setConfDb();
        TestConnectionProvider.createConnection();
    }

    @Before
    public void before() {
        new TableTransporter(dataSource).work();
        List<Segment> segments = segmentProvider.getSegment(dataSource.getSourceKey());
        new ColumnIndexer(dataSource, new ColumnKey(columnName), segments).work();
    }

    @Test
    public void testIndexSubDate() {
        for (GroupType type : SubDateColumn.TYPES_TO_GENERATE) {
            indexSubDate(type);
        }
    }

    private void indexSubDate(GroupType type) {
        List<Segment> segments = segmentProvider.getSegment(dataSource.getSourceKey());
        new SubDateColumnIndexer(dataSource, new ColumnKey(columnName), type, segments).work();

        assertTrue(!segments.isEmpty());

        Column origin = segments.get(0).getColumn(new ColumnKey(columnName));
        Column derive = new SubDateColumn((DateColumn) origin, type);

        DictionaryEncodedColumn originDict = origin.getDictionaryEncodedColumn();
        DictionaryEncodedColumn deriveDict = derive.getDictionaryEncodedColumn();

        Function dateDeriver = DateDerivers.newDeriver(type);
        for (int i = 1; i < originDict.size(); i++) {
            Object deriveVal = dateDeriver.apply(originDict.getValue(i));
            assertTrue(deriveDict.getIndex(deriveVal) != -1);
        }
    }
}