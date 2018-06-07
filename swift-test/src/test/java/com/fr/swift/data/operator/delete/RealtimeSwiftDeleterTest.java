package com.fr.swift.data.operator.delete;

import com.fr.annotation.Test;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.delete.RealtimeSwiftDeleter;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeSwiftDeleterTest extends BaseTest {

    @Test
    public void testRealtimeDeleteWithRealtime() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "RealtimeSwiftDeleterTest");
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

            assertEquals(segment.getRowCount(), 668);
            for (int i = 0; i < 668; i++) {
                assertTrue(segment.getAllShowIndex().contains(i));
            }

            putMetaAndSegment(dataSource, 0, location, Types.StoreType.MEMORY);

            for (String field : dataSource.getMetadata().getFieldNames()) {
                ColumnIndexer<?> indexer = new ColumnIndexer<>(dataSource, new ColumnKey(field), Collections.singletonList(segment));
                indexer.work();
            }

            //增量删除
            DataSource deleteDataSource = new QueryDBSource("select 合同ID from DEMO_CONTRACT where 合同类型 = '购买合同'", "RealtimeSwiftDeleterTest");
            SwiftSourceTransfer deleteTransfer = SwiftSourceTransferFactory.createSourceTransfer(deleteDataSource);
            SwiftResultSet deleteResultSet = deleteTransfer.createResultSet();
            List<Row> deleteRowList = new ArrayList<Row>();
            while (deleteResultSet.next()) {
                Row row = deleteResultSet.getRowData();
                deleteRowList.add(row);
            }
            RealtimeSwiftDeleter swiftDeleter = new RealtimeSwiftDeleter(segment);
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    @Test
    public void testRealtimeDeleteWithResultSet() {
        try {
            DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "RealtimeSwiftDeleterTest");
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            String cubePath = System.getProperty("user.dir") + "/cubes/" + dataSource.getSourceKey().getId() + "/seg1";
            IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.MEMORY);

            Segment segment = new RealTimeSegmentImpl(location, dataSource.getMetadata());
            List<Row> rowList = new ArrayList<Row>();

            while (resultSet.next()) {
                Row row = resultSet.getRowData();
                rowList.add(row);
            }
            SwiftRealtimeInserter swiftInserter = new SwiftRealtimeInserter(segment);
            swiftInserter.insertData(rowList);

            assertEquals(segment.getRowCount(), 668);
            for (int i = 0; i < 668; i++) {
                assertTrue(segment.getAllShowIndex().contains(i));
            }

            putMetaAndSegment(dataSource, 0, location, Types.StoreType.MEMORY);

            for (String field : dataSource.getMetadata().getFieldNames()) {
                ColumnIndexer<?> indexer = new ColumnIndexer<>(dataSource, new ColumnKey(field), Collections.singletonList(segment));
                indexer.work();
            }

            //增量删除
            DataSource deleteDataSource = new QueryDBSource("select 合同ID from DEMO_CONTRACT where 合同类型 = '购买合同'", "RealtimeSwiftDeleterTest");
            SwiftSourceTransfer deleteTransfer = SwiftSourceTransferFactory.createSourceTransfer(deleteDataSource);
            SwiftResultSet deleteResultSet = deleteTransfer.createResultSet();
            RealtimeSwiftDeleter swiftDeleter = new RealtimeSwiftDeleter(segment);
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}

