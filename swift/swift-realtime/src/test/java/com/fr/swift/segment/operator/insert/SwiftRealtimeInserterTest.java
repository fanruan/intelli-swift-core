package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.HistorySegment;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegment;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.test.TestResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author anchore
 * @date 2018/6/15
 */
public class SwiftRealtimeInserterTest {

    private DataSource dataSource;
    private SwiftSourceTransfer transfer;

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Before
    public void setUp() throws Exception {
        dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", SwiftRealtimeInserterTest.class.getName());
        transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);

        if (!com.fr.swift.db.impl.SwiftDatabase.getInstance().existsTable(dataSource.getSourceKey())) {
            com.fr.swift.db.impl.SwiftDatabase.getInstance().createTable(dataSource.getSourceKey(), dataSource.getMetadata());
        }
    }

    @Test
    public void test() throws Exception {
        RealTimeSegment realtimeSegment = getRealtimeSegment();
        Inserter inserter = new SwiftRealtimeInserter(realtimeSegment);
        inserter.insertData(transfer.createResultSet());

        HistorySegment historySegment = getBackupSegment(realtimeSegment);

        int rowCount = realtimeSegment.getRowCount();
        Assert.assertEquals(rowCount, historySegment.getRowCount());

        final ImmutableBitMap historyAllShowIndex = historySegment.getAllShowIndex();
        realtimeSegment.getAllShowIndex().traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                if (!historyAllShowIndex.contains(row)) {
                    Assert.fail();
                }
            }
        });

        for (String columnName : inserter.getFields()) {
            ColumnKey columnKey = new ColumnKey(columnName);
            Column<Object> realtimeColumn = realtimeSegment.getColumn(columnKey);
            Column<Object> historyColumn = historySegment.getColumn(columnKey);

            for (int i = 0; i < rowCount; i++) {
                Object value = realtimeColumn.getDetailColumn().get(i);

                Assert.assertEquals(value, historyColumn.getDetailColumn().get(i));

                if (value == null) {
                    Assert.assertTrue(historyColumn.getBitmapIndex().getNullIndex().contains(i));
                }

            }
        }
    }

    private HistorySegment getBackupSegment(RealTimeSegment realtimeSegment) {
        SwiftMetaData meta = realtimeSegment.getMetaData();
        String segPath = realtimeSegment.getLocation().getPath();
        SwiftDatabase swiftSchema = meta.getSwiftDatabase();
        return new HistorySegmentImpl(new ResourceLocation(segPath.replace(swiftSchema.getDir(), swiftSchema.getBackupDir()), StoreType.NIO), meta);
    }

    private RealTimeSegment getRealtimeSegment() {
        return new RealTimeSegmentImpl(new ResourceLocation(
                String.format("%s/%s/seg0", TestResource.getRunPath(getClass()), SwiftDatabase.CUBE.getDir()), StoreType.MEMORY), dataSource.getMetadata());
    }
}