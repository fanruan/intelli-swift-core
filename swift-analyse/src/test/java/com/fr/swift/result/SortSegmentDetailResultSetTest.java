package com.fr.swift.result;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class SortSegmentDetailResultSetTest {

    private int fetchSize = 30;
    private int rowCount = 100;
    private SortSegmentDetailResultSet rs;
    private List<Row> expected;

    @Before
    public void setUp() throws Exception {
        CubeData cubeData = new CubeData(2, 0, rowCount);
        DetailFilter filter = EasyMock.createMock(DetailFilter.class);
        EasyMock.expect(filter.createFilterIndex()).andReturn(BitMaps.newAllShowBitMap(rowCount)).anyTimes();
        EasyMock.replay(filter);
        Sort sort = new DescSort(1);
        List<Pair<Column, IndexInfo>> dimensions = cubeData.getDimensions();
        rs = new SortSegmentDetailResultSet(fetchSize, dimensions, filter, Collections.singletonList(sort));
        expected = new ArrayList<Row>();
        DetailColumn col1 = dimensions.get(0).getKey().getDetailColumn();
        DetailColumn col2 = dimensions.get(1).getKey().getDetailColumn();
        for (int i = 0; i < rowCount; i++) {
            List list = new ArrayList();
            list.add(col1.get(i));
            list.add(col2.get(i));
            expected.add(new ListBasedRow(list));
        }
        Collections.sort(expected, new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                return Comparators.<String>desc().compare((String) o1.getValue(1), (String) o2.getValue(1));
            }
        });
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
        for (int i = 0; i < rowCount; i++) {
            assertEquals(expected.get(i), rows.get(i));
        }
    }

    @Test
    public void getRowCount() {
        assertEquals(rowCount, rs.getRowCount());
    }

    @Test
    public void getMerger() {
        try {
            rs.getMerger();
            fail();
        } catch (Exception ignored) {
        }
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