package com.fr.swift.cloud.result;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.by.CubeData;
import com.fr.swift.cloud.result.detail.SegmentDetailResultSet;
import com.fr.swift.cloud.source.Row;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class SegmentDetailResultSetTest {

    private int fetchSize = 30;
    private int rowCount = 100;
    private SegmentDetailResultSet rs;

    @Before
    public void setUp() {
        CubeData cubeData = new CubeData(1, 0, 100);
        DetailFilter filter = EasyMock.createMock(DetailFilter.class);
        EasyMock.expect(filter.createFilterIndex()).andReturn(BitMaps.newAllShowBitMap(rowCount)).anyTimes();
        EasyMock.replay(filter);
        rs = new SegmentDetailResultSet(fetchSize, cubeData.getDimensions(), filter,null);
    }

    @Test
    public void getPage() {
        List<Row> rows = new ArrayList<Row>();
        int count = 0;
        while (rs.hasNextPage()) {
            count++;
            rows.addAll(rs.getPage());
        }
        assertEquals(4, count);
        assertEquals(rowCount, rows.size());
    }

    @Test
    public void getRowCount() {
        assertEquals(rowCount, rs.getRowCount());
    }

    @Test
    public void getFetchSize() {
        assertEquals(fetchSize, rs.getFetchSize());
    }

    @Test
    public void convert() {
        try {
            rs.convert(null);
            fail();
        } catch (Exception ignored) {
        }
    }
}