package com.fr.swift.segment;

import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.*;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.test.Preparer;
import com.fr.swift.test.TestResource;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/1
 */
public class SegmentResultSetTest {

    private final DataSource ds = new QueryDBSource("select 合同ID from DEMO_CONTRACT", getClass().getName());

    @Before
    public void setUp() {
        Preparer.prepareCubeBuild(getClass());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() throws Exception {
        IMocksControl control = EasyMock.createControl();
        SwiftResultSet mockSwiftResultSet = control.createMock(SwiftResultSet.class);
        EasyMock.expect(mockSwiftResultSet.getNextRow()).andAnswer(new IAnswer<Row>() {
            int i = 0;
            @Override
            public Row answer() throws Throwable {
                List list = new ArrayList();
                list.add(String.valueOf(i));
                i++;
                return new ListBasedRow(list);
            }
        }).times(5);
        EasyMock.expect(mockSwiftResultSet.hasNext()).andReturn(true).times(5);
        EasyMock.expect(mockSwiftResultSet.hasNext()).andReturn(false).anyTimes();
        EasyMock.expect(mockSwiftResultSet.getMetaData()).andReturn(null).anyTimes();
        control.replay();
        Segment seg = getSegment();
        Inserter inserter = new SwiftInserter(seg);
        inserter.insertData(mockSwiftResultSet);
        seg.putAllShowIndex(new RangeBitmap(1, 4));

        SwiftResultSet resultSet = new SegmentResultSet(seg);
        List<Row> rows = new ArrayList<>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }

        Assert.assertEquals(3, rows.size());
        Assert.assertEquals("1", rows.get(0).getValue(0));
        Assert.assertEquals("2", rows.get(1).getValue(0));
        Assert.assertEquals("3", rows.get(2).getValue(0));
    }

    private Segment getSegment() {
        return new RealTimeSegmentImpl(new ResourceLocation(String.format("%s/seg0", TestResource.getRunPath(getClass())), StoreType.MEMORY), ds.getMetadata());
    }
}