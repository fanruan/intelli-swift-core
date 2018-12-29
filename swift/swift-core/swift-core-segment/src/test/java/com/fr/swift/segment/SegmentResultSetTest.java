package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.test.TestResource;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/1
 */
public class SegmentResultSetTest {

    static class DemoContract {

        String contractId;

        static SwiftMetaData getMeta() {
            return new SwiftMetaDataBean("DEMO_CONTRACT",
                    Collections.<SwiftMetaDataColumn>singletonList(
                            new MetaDataColumnBean("合同ID", Types.VARCHAR)));
        }
    }

    private final SwiftMetaData meta = DemoContract.getMeta();

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Test
    public void test() throws Exception {
        SwiftResultSet resultSet = new LimitedResultSet(getResultSet(), 5);
        Segment seg = getSegment();
        Inserter inserter = SwiftContext.get().getBean("inserter", Inserter.class, seg);
        inserter.insertData(resultSet);
        seg.putAllShowIndex(new RangeBitmap(1, 4));

        resultSet = new SegmentResultSet(seg);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }

        Assert.assertEquals(3, rows.size());
        Assert.assertEquals("00c7b96a-bf9e-423f-9246-3898c6ac3150", rows.get(0).getValue(0));
        Assert.assertEquals("013b1dd8-1493-4c5b-8501-c0a78096d86a", rows.get(1).getValue(0));
        Assert.assertEquals("026e9c86-1af5-4d25-99b1-42e1a6c610d1", rows.get(2).getValue(0));
    }

    private Segment getSegment() {
        return SegmentUtils.newSegment(new ResourceLocation(String.format("%s/seg0", TestResource.getRunPath(getClass())), StoreType.MEMORY), meta);
    }

    private SwiftResultSet getResultSet() throws SQLException {
        SwiftResultSet resultSet = EasyMock.mock(SwiftResultSet.class);
        EasyMock.expect(resultSet.getMetaData()).andReturn(meta).anyTimes();

        EasyMock.expect(resultSet.hasNext()).andReturn(true).times(3).andReturn(false).anyTimes();

        EasyMock.expect(resultSet.getNextRow()).
                andReturn(new ListBasedRow("00c7b96a-bf9e-423f-9246-3898c6ac3150")).
                andReturn(new ListBasedRow("013b1dd8-1493-4c5b-8501-c0a78096d86a")).
                andReturn(new ListBasedRow("026e9c86-1af5-4d25-99b1-42e1a6c610d1")).
                andThrow(new IndexOutOfBoundsException());

        EasyMock.replay(resultSet);
        return resultSet;
    }
}