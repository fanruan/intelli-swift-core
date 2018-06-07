package com.fr.swift.data.operator.insert;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealSwiftInserterTest extends BaseTest {

    @Test
    public void testInsertDataNullIndex() throws Exception {
        DataSource dataSource = new QueryDBSource("select USER_NUMBER from DEMO_HR_USER", "RealSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg0";
        IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.MEMORY);
        Segment segment = new RealTimeSegmentImpl(location, dataSource.getMetadata());

        List<Row> rowList = new ArrayList<Row>();
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            rowList.add(row);
        }

        SwiftRealtimeInserter swiftInserter = new SwiftRealtimeInserter(segment);
        swiftInserter.insertData(rowList);

        for (int i = 0; i < 42; i++) {
            assertTrue(segment.getColumn(new ColumnKey("USER_NUMBER")).getBitmapIndex().getNullIndex().contains(i));
            assertTrue(segment.getColumn(new ColumnKey("USER_NUMBER")).getBitmapIndex().getBitMapIndex(0).contains(i));
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 42);
        assertTrue(true);
    }

    @Test
    public void testInsertDataWithList() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "RealSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg0";
        IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.MEMORY);
        Segment segment = new RealTimeSegmentImpl(location, dataSource.getMetadata());

        List<Row> rowList = new ArrayList<Row>();
        while (resultSet.next()) {
            Row row = resultSet.getRowData();
            rowList.add(row);
        }

        SwiftRealtimeInserter swiftInserter = new SwiftRealtimeInserter(segment);
        swiftInserter.insertData(rowList);

        for (int i = 0; i < 668; i++) {
            assertTrue(segment.getColumn(new ColumnKey("购买数量")).getDetailColumn().get(i) != null);
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 668);
        assertTrue(true);
    }

    @Test
    public void testInsertDataWithResult() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "RealSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg1";
        IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.MEMORY);
        Segment segment = new RealTimeSegmentImpl(location, dataSource.getMetadata());

        SwiftRealtimeInserter swiftInserter = new SwiftRealtimeInserter(segment);
        swiftInserter.insertData(resultSet);

        for (int i = 0; i < 668; i++) {
            assertTrue(segment.getColumn(new ColumnKey("购买数量")).getDetailColumn().get(i) != null);
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 668);
        assertTrue(true);
    }
}
