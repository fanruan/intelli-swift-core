package com.fr.swift.data.operator.insert;

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
import com.fr.swift.test.TestResource;
import com.fr.swift.util.FileUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class HisSwiftInserterTest extends BaseTest {

    private final String path = TestResource.getRunPath(getClass());
    protected List<Segment> segments = new ArrayList<Segment>();

    @Test
    public void testInsertDataNullIndex() throws Exception {
        DataSource dataSource = new QueryDBSource("select USER_NUMBER from DEMO_HR_USER", "HisSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = path + "/cubes/" + dataSource.getSourceKey().getId() + "/seg0";
        FileUtil.delete(cubePath);
        IResourceLocation location = new ResourceLocation(cubePath);
        Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());
        List<Row> rowList = new ArrayList<Row>();

        while (resultSet.next()) {
            Row row = resultSet.getNextRow();
            rowList.add(row);
        }

        HistorySwiftInserter swiftInserter = new HistorySwiftInserter(segment);
        swiftInserter.insertData(rowList);
        for (int i = 0; i < 42; i++) {
            assertTrue(segment.getColumn(new ColumnKey("USER_NUMBER")).getBitmapIndex().getNullIndex().contains(i));
            assertTrue(segment.getColumn(new ColumnKey("USER_NUMBER")).getBitmapIndex().getBitMapIndex(0).contains(i));
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 42);
    }

    @Test
    public void testInsertDataWithList() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "HisSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = path + "/cubes/" + dataSource.getSourceKey().getId() + "/seg0";
        FileUtil.delete(cubePath);
        IResourceLocation location = new ResourceLocation(cubePath);
        Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());

        List<Row> rowList = new ArrayList<Row>();
        while (resultSet.next()) {
            Row row = resultSet.getNextRow();
            rowList.add(row);
        }

        HistorySwiftInserter swiftInserter = new HistorySwiftInserter(segment);
        swiftInserter.insertData(rowList);

        for (int i = 0; i < 668; i++) {
            assertTrue(segment.getColumn(new ColumnKey("购买数量")).getDetailColumn().get(i) != null);
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 668);
    }

    @Test
    public void testInsertDataWithResultSet() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "HisSwiftInserterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = path + "/cubes/" + dataSource.getSourceKey().getId() + "/seg1";
        FileUtil.delete(cubePath);
        IResourceLocation location = new ResourceLocation(cubePath);
        Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());

        HistorySwiftInserter swiftInserter = new HistorySwiftInserter(segment);
        swiftInserter.insertData(resultSet);

        for (int i = 0; i < 668; i++) {
            assertTrue(segment.getColumn(new ColumnKey("购买数量")).getDetailColumn().get(i) != null);
            assertTrue(segment.getAllShowIndex().contains(i));
        }
        assertEquals(segment.getRowCount(), 668);
    }
}
