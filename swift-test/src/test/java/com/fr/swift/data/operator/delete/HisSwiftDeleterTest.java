package com.fr.swift.data.operator.delete;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.delete.HistorySwiftDeleter;
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
import java.util.Collections;
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
public class HisSwiftDeleterTest extends BaseTest {

    private final String path = TestResource.getRunPath(getClass());

    @Test
    public void testHisDeleteWithRowlist() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "HisSwiftDeleterTest");

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

        assertEquals(segment.getRowCount(), 668);
        for (int i = 0; i < 668; i++) {
            assertTrue(segment.getAllShowIndex().contains(i));
        }

        putMetaAndSegment(dataSource, 0, location, Types.StoreType.FINE_IO);

        for (String field : dataSource.getMetadata().getFieldNames()) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(field), Collections.singletonList(segment));
            indexer.work();
        }

        //增量删除
        DataSource deleteDataSource = new QueryDBSource("select 合同ID from DEMO_CONTRACT where 合同类型 = '购买合同'", "HisSwiftDeleterTest");
        SwiftSourceTransfer deleteTransfer = SwiftSourceTransferFactory.createSourceTransfer(deleteDataSource);
        SwiftResultSet deleteResultSet = deleteTransfer.createResultSet();
        List<Row> deleteRowList = new ArrayList<Row>();
        while (deleteResultSet.next()) {
            Row row = deleteResultSet.getNextRow();
            deleteRowList.add(row);
        }
        HistorySwiftDeleter swiftDeleter = new HistorySwiftDeleter(segment);
        swiftDeleter.deleteData(deleteRowList);
        //row count 不变
        assertEquals(segment.getRowCount(), 668);
        int showCount = 0;

        for (int i = 0; i < 668; i++) {
            try {
                if (segment.getAllShowIndex().contains(i)) {
                    showCount++;
                }
            } catch (Exception e) {
            }
        }
        //allshowindex改变
        assertEquals(segment.getRowCount() - showCount, deleteRowList.size());
    }

    @Test
    public void testHisDeleteWithResultSet() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "HisSwiftDeleterTest");

        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();

        String cubePath = path + "/cubes/" + dataSource.getSourceKey().getId() + "/seg1";
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

        assertEquals(segment.getRowCount(), 668);
        for (int i = 0; i < 668; i++) {
            assertTrue(segment.getAllShowIndex().contains(i));
        }

        putMetaAndSegment(dataSource, 0, location, Types.StoreType.FINE_IO);

        for (String field : dataSource.getMetadata().getFieldNames()) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(field), Collections.singletonList(segment));
            indexer.work();
        }

        //增量删除
        DataSource deleteDataSource = new QueryDBSource("select 合同ID from DEMO_CONTRACT where 合同类型 = '购买合同'", "HisSwiftDeleterTest");
        SwiftSourceTransfer deleteTransfer = SwiftSourceTransferFactory.createSourceTransfer(deleteDataSource);
        SwiftResultSet deleteResultSet = deleteTransfer.createResultSet();
//            List<Row> deleteRowList = new ArrayList<Row>();
//            while (deleteResultSet.next()) {
//                Row row = deleteResultSet.getRowData();
//                deleteRowList.add(row);
//            }
        HistorySwiftDeleter swiftDeleter = new HistorySwiftDeleter(segment);
        swiftDeleter.deleteData(deleteResultSet);
        //row count 不变
        assertEquals(segment.getRowCount(), 668);
        int showCount = 0;

        for (int i = 0; i < 668; i++) {
            try {
                if (segment.getAllShowIndex().contains(i)) {
                    showCount++;
                }
            } catch (Exception e) {
            }
        }
        //allshowindex改变
        assertEquals(segment.getRowCount() - showCount, 482);
    }
}
