package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.test.Preparer;
import com.fr.swift.test.TestResource;
import com.fr.swift.util.FileUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class SwiftInserterTest {
    private static DataSource dataSource;

    private static SwiftSourceTransfer transfer;

    private static String cubePath;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Preparer.prepareCubeBuild();
        dataSource = new QueryDBSource("select 客户状态 from DEMO_CUSTOMER", SwiftInserterTest.class.getSimpleName());
        transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        cubePath = String.format("%s/cubes/%s/seg0",
                TestResource.getRunPath(SwiftInserterTest.class), dataSource.getSourceKey().getId());
        FileUtil.delete(cubePath);
    }

    @Test
    public void insertData() throws Exception {
        IResourceLocation location = new ResourceLocation(cubePath);
        Segment segment = new HistorySegmentImpl(location, dataSource.getMetadata());

        SwiftResultSet resultSet = transfer.createResultSet();
        Inserter inserter = new SwiftInserter(segment);
        inserter.insertData(resultSet);

        resultSet = transfer.createResultSet();
        inserter = new SwiftInserter(segment);
        inserter.insertData(resultSet);

        int rowCount = segment.getRowCount();
        Column column = segment.getColumn(new ColumnKey("客户状态"));
        DetailColumn detail = column.getDetailColumn();
        for (int i = 0; i < rowCount / 2; i++) {
            Assert.assertEquals(detail.get(i), detail.get(i + rowCount / 2));
        }

        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        nullIndex.traversal(i -> {
            if (i < rowCount / 2 && !nullIndex.contains(i + rowCount / 2)) {
                Assert.fail();
            }
        });
    }
}