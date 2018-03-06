package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cal.Query;
import com.fr.swift.cal.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.cal.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.FakeStringDetailColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.source.Row;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailResultSetTest extends TestCase {

    private DetailFilter filter;
    private List<Column> columnList = new ArrayList<Column>();
    private ImmutableBitMap bitMap = RoaringMutableBitMap.newInstance();
    private List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();

    @Override
    public void setUp() throws Exception {
        super.setUp();

        IMocksControl control = EasyMock.createControl();

        Column mockColumnInt = control.createMock(Column.class);
        Column mockColumnDouble = control.createMock(Column.class);
        Column mockColumnLong = control.createMock(Column.class);
        Column mockColumnString = control.createMock(Column.class);
        IntDetailColumn mockIntColumn = control.createMock(IntDetailColumn.class);
        DoubleDetailColumn mockDoubleColumn = control.createMock(DoubleDetailColumn.class);
        LongDetailColumn mockLongColumn = control.createMock(LongDetailColumn.class);
        FakeStringDetailColumn mockStringColumn = control.createMock(FakeStringDetailColumn.class);
        filter = control.createMock(DetailFilter.class);

        ((MutableBitMap) bitMap).add(1);
        ((MutableBitMap) bitMap).add(4);
        ((MutableBitMap) bitMap).add(7);
        ((MutableBitMap) bitMap).add(9);

        EasyMock.expect(filter.createFilterIndex()).andReturn(bitMap).anyTimes();


        EasyMock.expect(mockColumnInt.getDetailColumn()).andReturn(mockIntColumn).anyTimes();
        EasyMock.expect(mockColumnDouble.getDetailColumn()).andReturn(mockDoubleColumn).anyTimes();
        EasyMock.expect(mockColumnLong.getDetailColumn()).andReturn(mockLongColumn).anyTimes();
        EasyMock.expect(mockColumnString.getDetailColumn()).andReturn(mockStringColumn).anyTimes();
        EasyMock.expect(mockIntColumn.get(1)).andReturn(10).anyTimes();
        EasyMock.expect(mockIntColumn.get(4)).andReturn(20).anyTimes();
        EasyMock.expect(mockIntColumn.get(7)).andReturn(20).anyTimes();
        EasyMock.expect(mockIntColumn.get(9)).andReturn(90).anyTimes();

        EasyMock.expect(mockDoubleColumn.get(1)).andReturn(1.0).anyTimes();
        EasyMock.expect(mockDoubleColumn.get(4)).andReturn(3.6).anyTimes();
        EasyMock.expect(mockDoubleColumn.get(7)).andReturn(4.8).anyTimes();
        EasyMock.expect(mockDoubleColumn.get(9)).andReturn(0.0).anyTimes();

        EasyMock.expect(mockLongColumn.get(1)).andReturn(3l).anyTimes();
        EasyMock.expect(mockLongColumn.get(4)).andReturn(54l).anyTimes();
        EasyMock.expect(mockLongColumn.get(7)).andReturn(2l).anyTimes();
        EasyMock.expect(mockLongColumn.get(9)).andReturn(10l).anyTimes();

        EasyMock.expect(mockStringColumn.get(1)).andReturn("we").anyTimes();
        EasyMock.expect(mockStringColumn.get(4)).andReturn("fc").anyTimes();
        EasyMock.expect(mockStringColumn.get(7)).andReturn("dsc").anyTimes();
        EasyMock.expect(mockStringColumn.get(9)).andReturn("sdfva").anyTimes();


        control.replay();
        columnList.add(mockColumnInt);
        columnList.add(mockColumnDouble);
        columnList.add(mockColumnLong);
        columnList.add(mockColumnString);

    }

    public void testSegmentGetRowData() throws SQLException {

        int i = 0;
        int[] intData = {10, 20, 20, 90};
        double[] doubleData = {1.0, 3.6, 4.8, 0.0};
        long[] longData = {3l, 54l, 2l, 10l};
        String[] strData = {"we", "fc", "dsc", "sdfva"};
        SegmentDetailResultSet rs = new SegmentDetailResultSet(columnList, filter);
        while (rs.next()) {
            Row row = rs.getRowData();
            assertEquals((int) row.getValue(0), intData[i]);
            assertEquals((double) row.getValue(1), doubleData[i]);
            assertEquals((long) row.getValue(2), longData[i]);
            assertEquals((String) row.getValue(3), strData[i]);
            i ++;
        }


    }

    public void testMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {10, 20, 20, 90};
        double[] doubleData = {1.0, 3.6, 4.8, 0.0};
        long[] longData = {3l, 54l, 2l, 10l};
        String[] strData = {"we", "fc", "dsc", "sdfva"};

        for (int j = 0; j < 3; j++) {
            queries.add(new NormalDetailSegmentQuery(columnList, filter));
        }
        MultiSegmentDetailResultSet mrs = new MultiSegmentDetailResultSet(queries);
        while (mrs.next()) {
            Row row = mrs.getRowData();
            assertEquals((int) row.getValue(0), intData[i]);
            assertEquals((double) row.getValue(1), doubleData[i]);
            assertEquals((long) row.getValue(2), longData[i]);
            assertEquals((String) row.getValue(3), strData[i]);
            i = (i + 1) % 4;
        }
    }

    public void testSortSegmentGetRowData() {
        int i = 0;
        int[] intData = {90, 20, 20, 10};
        double[] doubleData = {0.0, 4.8, 3.6, 1.0};
        long[] longData = {10l, 2l, 54l, 3l};
        String[] strData = {"sdfva", "dsc", "fc", "we"};
        IntList sortIndex = IntListFactory.createHeapIntList(4);
        List<SortType> sorts = new ArrayList<SortType>();
        sortIndex.add(0);
        sortIndex.add(3);
        sortIndex.add(1);
        sortIndex.add(2);
        sorts.add(SortType.DESC);
        sorts.add(SortType.DESC);
        sorts.add(SortType.NONE);
        sorts.add(SortType.ASC);
        DetailResultSet rs = new SortSegmentDetailResultSet(columnList, filter, sortIndex, sorts);

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((double) row.getValue(1), doubleData[i]);
                assertEquals((long) row.getValue(2), longData[i]);
                assertEquals((String) row.getValue(3), strData[i]);
                i ++;
            }
        } catch (Exception e) {

        }

    }

    public void testSortMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {90, 20, 20, 10};
        double[] doubleData = {0.0, 4.8, 3.6, 1.0};
        long[] longData = {10l, 2l, 54l, 3l};
        String[] strData = {"sdfva", "dsc", "fc", "we"};
        IntList sortIndex = IntListFactory.createHeapIntList(4);
        List<SortType> sorts = new ArrayList<SortType>();
        sortIndex.add(0);
        sortIndex.add(3);
        sortIndex.add(1);
        sortIndex.add(2);
        sorts.add(SortType.DESC);
        sorts.add(SortType.DESC);
        sorts.add(SortType.NONE);
        sorts.add(SortType.ASC);
        for (int j = 0; j < 3; j++) {
            queries.add(new SortDetailSegmentQuery(columnList, filter, sortIndex, sorts));
        }
        DetailResultSet rs = new SortMultiSegmentDetailResultSet(queries, ((SortSegmentDetailResultSet) queries.get(0).getQueryResult()).getDetailSortComparator());

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i / 3]);
                assertEquals((double) row.getValue(1), doubleData[i / 3]);
                assertEquals((long) row.getValue(2), longData[i / 3]);
                assertEquals((String) row.getValue(3), strData[i / 3]);
                i ++;
            }
        } catch (Exception e) {

        }
    }
}
