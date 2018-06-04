package com.fr.swift.result;


import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.Query;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.query.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.column.Column;
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
        intColumn = new CreateColumnList().getIntColumn();
        longColumn = new CreateColumnList().getLongColumn();
        doubleColumn = new CreateColumnList().getDoubleColumn();
        stringColumn = new CreateColumnList().getStringColumn();
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


    public void testSegmentGetRowData() {

        int i = 0;
        int[] intData = {2, 4, 2, 4};
        double[] doubleData = {9.5, 40.1, 9.5, 40.1};
        long[] longData = {12, 23, 23, 23};
        String[] strData = {"A", "C", "C", "A"};
        SegmentDetailResultSet rs = new SegmentDetailResultSet(columnList, filter, null);
        while (rs.next()) {
            Row row = rs.getRowData();
            assertEquals((int) row.getValue(0), intData[i]);
            assertEquals((long) row.getValue(1), longData[i]);
            assertEquals(row.getValue(2), doubleData[i]);
            assertEquals(row.getValue(3), strData[i]);
            i++;
        }


    }

    public void testMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {2, 4, 2, 4};
        double[] doubleData = {9.5, 40.1, 9.5, 40.1};
        long[] longData = {12, 23, 23, 23};
        String[] strData = {"A", "C", "C", "A"};

        for (int j = 0; j < 3; j++) {
            queries.add(new NormalDetailSegmentQuery(columnList, filter, null));
        }
        MultiSegmentDetailResultSet mrs = new MultiSegmentDetailResultSet(queries, null);
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
        long[] longData = {23, 23, 23, 12};
        String[] strData = {"C", "A", "C", "A"};
        IntList sortIndex = IntListFactory.createHeapIntList(3);
        List<SortType> sorts = new ArrayList<SortType>();
        sortIndex.add(0);
        sortIndex.add(3);
        sortIndex.add(1);

        sorts.add(SortType.DESC);
        sorts.add(SortType.DESC);
        sorts.add(SortType.ASC);
        DetailResultSet rs = new SortSegmentDetailResultSet(columnList, filter, sortIndex, sorts, null);

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((long) row.getValue(1), longData[i]);
                assertEquals(row.getValue(2), doubleData[i]);
                assertEquals(row.getValue(3), strData[i]);
                i++;
            }
        } catch (Exception e) {

        }

    }

    public void testSortMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {4, 4, 2, 2};
        double[] doubleData = {40.1, 40.1, 9.5, 9.5};
        long[] longData = {23, 23, 23, 12};
        String[] strData = {"C", "A", "C", "A"};
        IntList sortIndex = IntListFactory.createHeapIntList(3);
        List<SortType> sorts = new ArrayList<SortType>();
        sortIndex.add(0);
        sortIndex.add(3);
        sortIndex.add(1);
        sorts.add(SortType.DESC);
        sorts.add(SortType.DESC);
        sorts.add(SortType.ASC);
        for (int j = 0; j < 3; j++) {
            queries.add(new SortDetailSegmentQuery(columnList, filter, sortIndex, sorts, null));
        }
        Comparator comparator = new Comparator <Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                for (int i = 0; i < sortIndex.size(); i++) {
                    int c = 0;
                    //比较的列先后顺序
                    int realColumn = sortIndex.get(i);
                    if (sorts.get(i) == SortType.ASC) {
                        c = columnList.get(realColumn).getDictionaryEncodedColumn().getComparator().compare(o1.getValue(realColumn), o2.getValue(realColumn));
                    }
                    if (sorts.get(i) == SortType.DESC) {
                        c = Comparators.reverse(columnList.get(realColumn).getDictionaryEncodedColumn().getComparator()).compare(o1.getValue(realColumn), o2.getValue(realColumn));
                    }
                    if (c != 0) {
                        return c;
                    }
                }
                return 0;
            }
        };
        DetailResultSet rs = new SortMultiSegmentDetailResultSet(queries, comparator, null);
        //测试索引排序，单块数据量大于3000时使用索引排序
//        DetailResultSet rs = new SortMultiSegmentDetailResultSet(queries, comparator);

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i / 3]);
                assertEquals((long) row.getValue(1), longData[i / 3]);
                assertEquals(row.getValue(2), doubleData[i / 3]);
                assertEquals(row.getValue(3), strData[i / 3]);
                i++;
            }
        } catch (Exception e) {

        }
    }

    public void testSortSegmentByIndexGetRowData() {
        int i = 0;
        int[] intData = {2, 4, 4, 2};
        double[] doubleData = {9.5, 40.1, 40.1, 9.5};
        long[] longData = {23, 23, 23, 12};
        String[] strData = {"C", "C", "A", "A"};
        IntList sortIndex = IntListFactory.createHeapIntList(3);
        List<SortType> sorts = new ArrayList<SortType>();

        sortIndex.add(1);
        sortIndex.add(0);
        sorts.add(SortType.DESC);
        sorts.add(SortType.ASC);

        DetailResultSet rs = new SortSegmentDetailByIndexResultSet(columnList, filter, sortIndex, sorts, null);

        try {
            while (rs.next()) {
                Row row = rs.getRowData();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((long) row.getValue(1), longData[i]);
                assertEquals(row.getValue(2), doubleData[i]);
                assertEquals(row.getValue(3), strData[i]);
                i++;
            }
        } catch (Exception e) {

        }

    }
}
