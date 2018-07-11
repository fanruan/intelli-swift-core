package com.fr.swift.result;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.query.segment.detail.SortDetailSegmentQuery;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DetailResultSetTest extends TestCase {

    private DetailFilter filter;
    private List<Column> columnList = new ArrayList<Column>();
    private MutableBitMap bitMap = BitSetMutableBitMap.newInstance();
    private List<Query<DetailResultSet>> queries = new ArrayList<Query<DetailResultSet>>();
    private Column<Integer> intColumn;
    private Column<Long> longColumn;
    private Column<Double> doubleColumn;
    private Column<String> stringColumn;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        intColumn = new Column<Integer>() {
            @Override
            public DictionaryEncodedColumn<Integer> getDictionaryEncodedColumn() {
                int[] intDetail = {2, 2, 4, 3, 2, 3, 4, 2};
                List<Integer> keys = Arrays.asList(2, 3, 4);
                final int[] index = {0, 0, 2, 1, 0, 1, 2, 0};
                return new DictionaryEncodedColumn<Integer>() {
                    @Override
                    public void putSize(int size) {
                    }

                    @Override
                    public int size() {
                        return keys.size();
                    }

                    @Override
                    public void putGlobalSize(int globalSize) {
                    }

                    @Override
                    public int globalSize() {
                        return keys.size();
                    }

                    @Override
                    public void putValue(int index, Integer val) {
                    }

                    @Override
                    public Integer getValue(int index) {
                        return keys.get(index);
                    }

                    @Override
                    public Integer getValueByRow(int row) {
                        return intDetail[row];
                    }

                    @Override
                    public int getIndex(Object value) {
                        return keys.indexOf(value);
                    }

                    @Override
                    public void putIndex(int row, int index) {
                    }

                    @Override
                    public int getIndexByRow(int row) {
                        return keys.indexOf(intDetail[row]);
                    }

                    @Override
                    public void putGlobalIndex(int index, int globalIndex) {

                    }

                    @Override
                    public int getGlobalIndexByIndex(int index) {
                        return 0;
                    }

                    @Override
                    public int getGlobalIndexByRow(int row) {
                        return 0;
                    }

                    @Override
                    public Comparator<Integer> getComparator() {
                        return Integer::compareTo;
                    }

                    @Override
                    public ColumnTypeConstants.ClassType getType() {
                        return ColumnTypeConstants.ClassType.INTEGER;
                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void release() {

                    }
                };
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return null;
            }

            @Override
            public DetailColumn<Integer> getDetailColumn() {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
        longColumn = new Column<Long>() {
            @Override
            public DictionaryEncodedColumn<Long> getDictionaryEncodedColumn() {

                return new DictionaryEncodedColumn<Long>() {
                    long[] longDetail = {12, 18, 23, 18, 23, 18, 23, 12};
                    List<Long> keys = Arrays.asList(12L, 18L, 23L);
                    int[] index = {0, 1, 2, 1, 2, 1, 2, 0};

                    @Override
                    public void putSize(int size) {
                    }

                    @Override
                    public int size() {
                        return keys.size();
                    }

                    @Override
                    public void putGlobalSize(int globalSize) {
                    }

                    @Override
                    public int globalSize() {
                        return keys.size();
                    }

                    @Override
                    public void putValue(int index, Long val) {
                    }

                    @Override
                    public Long getValue(int index) {
                        return keys.get(index);
                    }

                    @Override
                    public Long getValueByRow(int row) {
                        return longDetail[row];
                    }

                    @Override
                    public int getIndex(Object value) {
                        return keys.indexOf(value);
                    }

                    @Override
                    public void putIndex(int row, int index) {
                    }

                    @Override
                    public int getIndexByRow(int row) {
                        return keys.indexOf(longDetail[row]);
                    }

                    @Override
                    public void putGlobalIndex(int index, int globalIndex) {

                    }

                    @Override
                    public int getGlobalIndexByIndex(int index) {
                        return 0;
                    }

                    @Override
                    public int getGlobalIndexByRow(int row) {
                        return 0;
                    }

                    @Override
                    public Comparator<Long> getComparator() {
                        return Long::compareTo;
                    }

                    @Override
                    public ColumnTypeConstants.ClassType getType() {
                        return ColumnTypeConstants.ClassType.LONG;
                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void release() {

                    }
                };
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return null;
            }

            @Override
            public DetailColumn<Long> getDetailColumn() {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
        doubleColumn = new Column<Double>() {
            @Override
            public DictionaryEncodedColumn<Double> getDictionaryEncodedColumn() {
                return new DictionaryEncodedColumn<Double>() {
                    double[] doubleDetail = {9.5, 50.2, 40.1, 12.3, 9.5, 12.3, 40.1, 9.5};
                    List<Double> keys = Arrays.asList(9.5, 12.3, 40.1, 50.2);
                    final int[] index = {0, 3, 2, 1, 0, 1, 2, 0};

                    @Override
                    public void putSize(int size) {
                    }

                    @Override
                    public int size() {
                        return keys.size();
                    }

                    @Override
                    public void putGlobalSize(int globalSize) {
                    }

                    @Override
                    public int globalSize() {
                        return keys.size();
                    }

                    @Override
                    public void putValue(int index, Double val) {
                    }

                    @Override
                    public Double getValue(int index) {
                        return keys.get(index);
                    }

                    @Override
                    public Double getValueByRow(int row) {
                        return doubleDetail[row];
                    }

                    @Override
                    public int getIndex(Object value) {
                        return keys.indexOf(value);
                    }

                    @Override
                    public void putIndex(int row, int index) {
                    }

                    @Override
                    public int getIndexByRow(int row) {
                        return keys.indexOf(doubleDetail[row]);
                    }

                    @Override
                    public void putGlobalIndex(int index, int globalIndex) {

                    }

                    @Override
                    public int getGlobalIndexByIndex(int index) {
                        return 0;
                    }

                    @Override
                    public int getGlobalIndexByRow(int row) {
                        return 0;
                    }

                    @Override
                    public Comparator<Double> getComparator() {
                        return Double::compareTo;
                    }

                    @Override
                    public ColumnTypeConstants.ClassType getType() {
                        return ColumnTypeConstants.ClassType.DOUBLE;
                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void release() {

                    }
                };
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return null;
            }

            @Override
            public DetailColumn<Double> getDetailColumn() {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
        stringColumn = new Column<String>() {
            @Override
            public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
                return new DictionaryEncodedColumn<String>() {
                    String strDetail[] = {"A", "B", "C", "B", "C", "B", "A", "C"};
                    final String[] keys = {"A", "B", "C"};
                    final int[] index = {0, 1, 2, 1, 2, 1, 0, 2};

                    @Override
                    public void putSize(int size) {
                    }

                    @Override
                    public int size() {
                        return keys.length;
                    }

                    @Override
                    public void putGlobalSize(int globalSize) {
                    }

                    @Override
                    public int globalSize() {
                        return keys.length;
                    }

                    @Override
                    public void putValue(int index, String val) {
                    }

                    @Override
                    public String getValue(int index) {
                        return keys[index];
                    }

                    @Override
                    public String getValueByRow(int row) {
                        return strDetail[row];
                    }

                    @Override
                    public int getIndex(Object value) {
                        return Arrays.asList(keys).indexOf(value);
                    }

                    @Override
                    public void putIndex(int row, int index) {
                    }

                    @Override
                    public int getIndexByRow(int row) {
                        return Arrays.asList(keys).indexOf(strDetail[row]);
                    }

                    @Override
                    public void putGlobalIndex(int index, int globalIndex) {

                    }

                    @Override
                    public int getGlobalIndexByIndex(int index) {
                        return 0;
                    }

                    @Override
                    public int getGlobalIndexByRow(int row) {
                        return 0;
                    }

                    @Override
                    public Comparator<String> getComparator() {
                        return String::compareTo;
                    }

                    @Override
                    public ColumnTypeConstants.ClassType getType() {
                        return ColumnTypeConstants.ClassType.STRING;
                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void release() {

                    }
                };
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return null;
            }

            @Override
            public DetailColumn<String> getDetailColumn() {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };

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
        DetailResultSet rs = new SegmentDetailResultSet(columnList, filter, null);
        try {
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((long) row.getValue(1), longData[i]);
                assertEquals(row.getValue(2), doubleData[i]);
                assertEquals(row.getValue(3), strData[i]);
                i++;
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {2, 4, 2, 4};
        long[] longData = {12, 23, 23, 23};
        double[] doubleData = {9.5, 40.1, 9.5, 40.1};
        String[] strData = {"A", "C", "C", "A"};

        for (int j = 0; j < 3; j++) {
            queries.add(new NormalDetailSegmentQuery(columnList, filter, null));
        }
        MultiSegmentDetailResultSet mrs = new MultiSegmentDetailResultSet(queries, null);
        while (mrs.hasNext()) {
            Row row = mrs.getNextRow();
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
        long[] longData = {23, 23, 12, 23};
        double[] doubleData = {40.1, 40.1, 9.5, 9.5};
        String[] strData = {"C", "A", "A", "C"};
        List<Sort> sorts = new ArrayList<>();
        sorts.add(new DescSort(0));
        sorts.add(new AscSort(1));
        sorts.add(new DescSort(3));
        DetailResultSet rs = new SortSegmentDetailResultSet(columnList, filter, sorts, null);

//      [2, 12, 9.5, A]  => [4, 23, 40.1, C]
//      [4, 23, 40.1, C]    [4, 23, 40.1, A]
//      [2, 23, 9.5, C]     [2, 12, 9.5, A]
//      [4, 23, 40.1, A]    [2, 23, 9.5, C]
        try {
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                assertEquals((int) row.getValue(0), intData[i]);
                assertEquals((long) row.getValue(1), longData[i]);
                assertEquals(row.getValue(2), doubleData[i]);
                assertEquals(row.getValue(3), strData[i]);
                i++;
            }
        } catch (Exception e) {
            assertTrue(false);
        }

    }

    public void testSortMultiSegmentGetRowData() throws SQLException {
        int i = 0;
        int[] intData = {4, 4, 2, 2};
        long[] longData = {23, 23, 12, 23};
        double[] doubleData = {40.1, 40.1, 9.5, 9.5};
        String[] strData = {"C", "A", "A", "C"};
        List<Sort> sorts = new ArrayList<>();
        sorts.add(new DescSort(0));
        sorts.add(new AscSort(1));
        sorts.add(new DescSort(3));
        for (int j = 0; j < 3; j++) {
            queries.add(new SortDetailSegmentQuery(columnList, filter, sorts, null));
        }
        List<Pair<Sort, Comparator>> pairs = new ArrayList<>();
        pairs.add(Pair.of(new DescSort(0), Comparators.<Integer>asc()));
        pairs.add(Pair.of(new AscSort(1), Comparators.<String>asc()));
        pairs.add(Pair.of(new DescSort(3), Comparators.<Double>asc()));
        DetailResultSet rs = new SortMultiSegmentDetailResultSet(queries, pairs, null);
        try {
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                assertEquals((int) row.getValue(0), intData[i / 3]);
                assertEquals((long) row.getValue(1), longData[i / 3]);
                assertEquals(row.getValue(2), doubleData[i / 3]);
                assertEquals(row.getValue(3), strData[i / 3]);
                i++;
            }
        } catch (Exception e) {

        }
    }
}
