package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.exception.SegmentAbsentException;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class SwiftInserterTest {

    private DataSource dataSource;

    private SwiftSourceTransfer transfer;

    private String cubePath;

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Rule
    public TestRule getReleasableLeakVerifier() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.ReleasableLeakVerifier").newInstance();
    }

    @Before
    public void beforeClass() throws SegmentAbsentException {
        dataSource = new QueryDBSource("select 客户状态 from DEMO_CUSTOMER", SwiftInserterTest.class.getSimpleName());
        transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        cubePath = String.format("cubes/%s/seg0", dataSource.getSourceKey().getId());
    }


    @Test
    public void insertData() throws Exception {
        IResourceLocation location = new ResourceLocation(cubePath);
        Segment segment = SegmentUtils.newSegment(location, dataSource.getMetadata());

        SwiftResultSet resultSet = transfer.createResultSet();
        Inserter inserter = new SwiftInserter(segment);
        inserter.insertData(resultSet);
        final int rowCount = segment.getRowCount();
        Assert.assertEquals(rowCount, 2);
        Column column = segment.getColumn(new ColumnKey("客户状态"));
        final ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        nullIndex.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int i) {
                if (i < rowCount / 2 && !nullIndex.contains(i + rowCount / 2)) {
                    Assert.fail();
                }
            }
        });
    }
}