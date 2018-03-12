package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetImmutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cal.Query;
import com.fr.swift.cal.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.cal.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
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
import java.util.Comparator;
import java.util.List;

public class DetailResultSetTest extends TestCase {

    private DetailFilter filter;
    private List<Column> columnList = new ArrayList<Column>();
    private MutableBitMap bitMap = BitSetMutableBitMap.newInstance();
    private List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
    private Column intColumn;
    private Column longColumn;
    private Column doubleColumn;
    private Column stringColumn;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        intColumn = new CreateColumnListForDetailResultSet().getIntColumn();
        longColumn = new CreateColumnListForDetailResultSet().getLongColumn();
        doubleColumn = new CreateColumnListForDetailResultSet().getDoubleColumn();
        stringColumn = new CreateColumnListForDetailResultSet().getStringColumn();
        IMocksControl control = EasyMock.createControl();


        filter = control.createMock(DetailFilter.class);

        bitMap.add(0);
        bitMap.add(2);
        bitMap.add(4);
        bitMap.add(6);

        EasyMock.expect(filter.createFilterIndex()).andReturn(bitMap).anyTimes();


        control.replay();
        columnList.add(intColumn);
        columnList.add(longColumn);
        columnList.add(doubleColumn);
        columnList.add(stringColumn);


    }


    public void testSegmentGetRowData() throws SQLException {

        int i = 0;
        int[] intData = {2, 4, 2, 4};
        double[] doubleData = {9.5, 40.1, 9.5, 40.1};
        long[] longData = {12, 23, 23, 23};
        String[] strData = {"A", "C", "C", "A"};
        SegmentDetailResultSet rs = new SegmentDetailResultSet(columnList, filter);
        while (rs.next()) {
            Row row = rs.getRowData();
            assertEquals((int) row.getValue(0), intData[i]);
            assertEquals((long) row.getValue(1), longData[i]);
            assertEquals( row.getValue(2), doubleData[i]);
            assertEquals(row.getValue(3), strData[i]);
            i ++;
        }


    }

    public void testMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {2, 4, 2, 4};
        double[] doubleData = {9.5, 40.1, 9.5, 40.1};
        long[] longData = {12, 23, 23, 23};
        String[] strData = {"A", "C", "C", "A"};

        for (int j = 0; j < 3; j++) {
            queries.add(new NormalDetailSegmentQuery(columnList, filter));
        }
        MultiSegmentDetailResultSet mrs = new MultiSegmentDetailResultSet(queries);
        while (mrs.next()) {
            Row row = mrs.getRowData();
            assertEquals((int) row.getValue(0), intData[i]);
            assertEquals((long) row.getValue(1), longData[i]);
            assertEquals(row.getValue(2), doubleData[i]);
            assertEquals(row.getValue(3), strData[i]);
            i = (i + 1) % 4;
        }
    }

    public void testSortSegmentGetRowData() {
        int i = 0;
        int[] intData = {4, 4, 2, 2};
        double[] doubleData = {40.1, 40.1, 9.5, 9.5};
        long[] longData = {23, 23, 12, 23};
        String[] strData = {"A", "C", "A", "C"};
        IntList sortIndex = IntListFactory.createHeapIntList(3);
        List<SortType> sorts = new ArrayList<SortType>();
        sortIndex.add(0);
        sortIndex.add(3);
        sortIndex.add(1);

        sorts.add(SortType.DESC);
        sorts.add(SortType.DESC);
        sorts.add(SortType.NONE);
        sorts.add(SortType.ASC);
        DetailResultSet rs = new SortSegmentDetailResultSet(columnList, filter, sortIndex, sorts);

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((long) row.getValue(1), longData[i]);
                assertEquals(row.getValue(2), doubleData[i]);
                assertEquals(row.getValue(3), strData[i]);
                i ++;
            }
        } catch (Exception e) {

        }

    }

    public void testSortMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {4, 4, 2, 2};
        double[] doubleData = {40.1, 40.1, 9.5, 9.5};
        long[] longData = {23, 23, 12, 23};
        String[] strData = {"A", "C", "A", "C"};
        IntList sortIndex = IntListFactory.createHeapIntList(3);
        List<SortType> sorts = new ArrayList<SortType>();
        sortIndex.add(0);
        sortIndex.add(3);
        sortIndex.add(1);
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
                assertEquals((long) row.getValue(1), longData[i / 3]);
                assertEquals(row.getValue(2), doubleData[i / 3]);
                assertEquals(row.getValue(3), strData[i / 3]);
                i ++;
            }
        } catch (Exception e) {

        }
    }

    public void testSortSegmentByIndexGetRowData() {
        int i = 0;
        int[] intData = {2, 4, 4, 2};
        double[] doubleData = {9.5, 40.1, 40.1, 9.5};
        long[] longData = {12, 23, 23, 23};
        String[] strData = {"A", "C", "A", "C"};
        IntList sortIndex = IntListFactory.createHeapIntList(3);
        List<SortType> sorts = new ArrayList<SortType>();

        sortIndex.add(1);
        sortIndex.add(0);
        sorts.add(SortType.DESC);
        sorts.add(SortType.ASC);
        sorts.add(SortType.NONE);
        sorts.add(SortType.NONE);

        DetailResultSet rs = new SortSegmentDetailByIndexResultSet(columnList, filter, sortIndex, sorts);

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((long) row.getValue(1), longData[i]);
                assertEquals(row.getValue(2), doubleData[i]);
                assertEquals(row.getValue(3), strData[i]);
                i ++;
            }
        } catch (Exception e) {

        }

    }
}
