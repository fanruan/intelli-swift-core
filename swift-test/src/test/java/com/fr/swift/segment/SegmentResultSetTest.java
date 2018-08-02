package com.fr.swift.segment;

import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.test.Preparer;
import com.fr.swift.test.TestResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/1
 */
public class SegmentResultSetTest {

    private final DataSource ds = new QueryDBSource("select 合同ID from DEMO_CONTRACT", getClass().getName());

    @BeforeClass
    public static void setUp() throws Exception {
        Preparer.prepareCubeBuild();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() throws Exception {
        SwiftResultSet resultSet = new LimitedResultSet(SwiftSourceTransferFactory.createSourceTransfer(ds).createResultSet(), 5);
        Segment seg = getSegment();
        Inserter inserter = new SwiftInserter(seg);
        inserter.insertData(resultSet);
        seg.putAllShowIndex(new RangeBitmap(1, 4));

        resultSet = new SegmentResultSet(seg);
        List<Row> rows = new ArrayList<>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }

        Assert.assertEquals(3, rows.size());
        Assert.assertEquals("00c7b96a-bf9e-423f-9246-3898c6ac3150", rows.get(0).getValue(0));
        Assert.assertEquals("013b1dd8-1493-4c5b-8501-c0a78096d86a", rows.get(1).getValue(0));
        Assert.assertEquals("026e9c86-1af5-4d25-99b1-42e1a6c610d1", rows.get(2).getValue(0));
    }

    private Segment getSegment() {
        return new RealTimeSegmentImpl(new ResourceLocation(String.format("%s/seg0", TestResource.getRunPath(getClass())), StoreType.MEMORY), ds.getMetadata());
    }
}