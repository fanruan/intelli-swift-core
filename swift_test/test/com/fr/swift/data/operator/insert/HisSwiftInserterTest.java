package com.fr.swift.data.operator.insert;

import com.fr.annotation.Test;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.insert.HistorySwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class HisSwiftInserterTest extends BaseTest {

    protected List<Segment> segments = new ArrayList<Segment>();

    @Test
    public void testInsertDataNullIndex() {
        DataSource dataSource = new QueryDBSource("select USER_NUMBER from DEMO_HR_USER", "HisSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg0";
        IResourceLocation location = new ResourceLocation(cubePath);
        Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());
        List<Row> rowList = new ArrayList<Row>();
        try {
            while (resultSet.next()) {
                Row row = resultSet.getRowData();
                rowList.add(row);
            }

            HistorySwiftInserter swiftInserter = new HistorySwiftInserter(segment);
            swiftInserter.insertData(rowList);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
        for (int i = 0; i < 42; i++) {
            assertTrue(segment.getColumn(new ColumnKey("USER_NUMBER")).getBitmapIndex().getNullIndex().contains(i));
            assertTrue(segment.getColumn(new ColumnKey("USER_NUMBER")).getBitmapIndex().getBitMapIndex(0).contains(i));
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 42);
        assertTrue(true);
    }

    @Test
    public void testInsertDataWithList() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "HisSwiftInserterTest");

            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();

            String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg0";
            IResourceLocation location = new ResourceLocation(cubePath);
            Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());

            List<Row> rowList = new ArrayList<Row>();
            while (resultSet.next()) {
                Row row = resultSet.getRowData();
                rowList.add(row);
            }

            HistorySwiftInserter swiftInserter = new HistorySwiftInserter(segment);
            swiftInserter.insertData(rowList);

            for (int i = 0; i < 668; i++) {
                assertTrue(segment.getColumn(new ColumnKey("购买数量")).getDetailColumn().get(i) != null);
                assertTrue(segment.getAllShowIndex().contains(i));
            }
            assertEquals(segment.getRowCount(), 668);
            assertTrue(true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    @Test
    public void testInsertDataWithResultSet() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "HisSwiftInserterTest");

            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();

            String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg1";
            IResourceLocation location = new ResourceLocation(cubePath);
            Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());

            HistorySwiftInserter swiftInserter = new HistorySwiftInserter(segment);
            swiftInserter.insertData(resultSet);

            for (int i = 0; i < 668; i++) {
                assertTrue(segment.getColumn(new ColumnKey("购买数量")).getDetailColumn().get(i) != null);
                assertTrue(segment.getAllShowIndex().contains(i));
            }
            assertEquals(segment.getRowCount(), 668);
            assertTrue(true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
