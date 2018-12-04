package com.fr.swift.segment.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.resultset.LimitedResultSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.List;

/**
 * @author anchore
 * @date 2018/8/7
 */
public class HistoryBlockInserterTest {

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Test
    public void test() throws Exception {
        DataSource ds = new TableDBSource("DEMO_CONTRACT", getClass().getName());
        SwiftResultSet resultSet = new LimitedResultSet(SwiftSourceTransferFactory.createSourceTransfer(ds).createResultSet(), 24);
        new HistoryBlockInserter(ds, new LineSourceAlloter(ds.getSourceKey(), new LineAllotRule(5))).insertData(resultSet);

        List<Segment> segs = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(ds.getSourceKey());

        Assert.assertEquals(5, segs.size());

        resultSet = new LimitedResultSet(SwiftSourceTransferFactory.createSourceTransfer(ds).createResultSet(), 24);

        SwiftMetaData meta = ds.getMetadata();

        for (Segment seg : segs.subList(0, 4)) {
            int rowCount = seg.getRowCount();
            Assert.assertEquals(5, rowCount);
            for (int i = 0; i < rowCount; i++) {
                Row row = resultSet.getNextRow();
                for (int j = 0; j < meta.getColumnCount(); j++) {
                    DetailColumn<Object> detail = seg.getColumn(new ColumnKey(meta.getColumnName(j + 1))).getDetailColumn();
                    Assert.assertEquals(row.getValue(j), detail.get(i));
                }
            }
        }
        int rowCount = segs.get(4).getRowCount();
        Assert.assertEquals(4, rowCount);
        for (int i = 0; i < rowCount; i++) {
            Row row = resultSet.getNextRow();
            for (int j = 0; j < meta.getColumnCount(); j++) {
                DetailColumn<Object> detail = segs.get(4).getColumn(new ColumnKey(meta.getColumnName(j + 1))).getDetailColumn();
                Assert.assertEquals(row.getValue(j), detail.get(i));
            }
        }

    }
}