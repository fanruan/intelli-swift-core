package com.fr.swift.generate.record;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.record.RealtimeRecorder;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.QuerySourceTransfer;
import com.fr.swift.source.db.TestConnectionProvider;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * This class created on 2018/5/24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RealtimeRecorderTest extends BaseTest {

    private QueryDBSource dataSource;

    private ConnectionInfo connectionInfo;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        connectionInfo = TestConnectionProvider.createConnection();
        dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "RealtimeRecorderTest");
//        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir") + "\\" + System.currentTimeMillis()));
    }

    @Test
    public void testRecord() throws SQLException {
        List<String> fields = dataSource.getMetadata().getFieldNames();
        RealtimeRecorder realtimeRecorder = new RealtimeRecorder(dataSource.getSourceKey(),
                dataSource.getMetadata(), fields, dataSource.getSourceKey().getId());

        QuerySourceTransfer transfer = new QuerySourceTransfer(connectionInfo, dataSource.getMetadata(), dataSource.getMetadata(), dataSource.getQuery());
        SwiftResultSet swiftResultSet = transfer.createResultSet();
        int rowCount = 0;
        while (swiftResultSet.next()) {
            Row row = swiftResultSet.getRowData();
            realtimeRecorder.recordData(row, rowCount / 100);
            rowCount++;
        }
        realtimeRecorder.end();

        for (int i = 0; i < 7; i++) {
            String cubePath = String.format("%s/%s/seg%d",
                    Schema.BACKUP_CUBE.getDir(),
                    dataSource.getSourceKey().getId(), i);
            IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.FINE_IO);
            Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());

            int count = segment.getRowCount();
            assertNotSame(count, 0);
            for (int j = 0; j < count; j++) {
                assertNotNull(segment.getColumn(new ColumnKey("付款金额")).getDetailColumn().get(i));
//                    for (int k = 0; k < fields.size(); k++) {
//                        segment.getColumn(new ColumnKey(fields.get(k))).getDetailColumn().get(i);
//                    }
            }
        }

    }
}
