package com.fr.swift.source.etl.join;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentTest;
import com.fr.swift.source.etl.BaseCreateSegment2Test;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 14:49
 */
public class JoinOperatorTest extends TestCase {

    List<JoinColumn> joinList = new ArrayList<JoinColumn>();
    ColumnKey[] lColumnKey = new ColumnKey[2];
    ColumnKey[] rColumnKey = new ColumnKey[2];
    Segment[] lSegment = new Segment[2];
    Segment[] rSegment = new Segment[2];

    public void setUp() throws Exception {
        SwiftMetaData metaData1 = EasyMock.createMock(SwiftMetaData.class);
        SwiftMetaData metaData2 = EasyMock.createMock(SwiftMetaData.class);
        ColumnKey key1 = new ColumnKey("column1");
        ColumnKey key2 = new ColumnKey("column2");
        ColumnKey key3 = new ColumnKey("column3");
        ColumnKey key4 = new ColumnKey("column4");
        lColumnKey[0] = key1;
        lColumnKey[1] = key2;
        rColumnKey[0] = key3;
        rColumnKey[1] = key4;
        lSegment[0] = new BaseCreateSegmentTest().getSegment(metaData1);
        lSegment[1] = new BaseCreateSegmentTest().getSegment(metaData1);
        rSegment[0] = new BaseCreateSegment2Test().getSegment(metaData2);
        rSegment[1] = new BaseCreateSegment2Test().getSegment(metaData2);
        DataSource parent1 = EasyMock.createMock(DataSource.class);
        DataSource parent2 = EasyMock.createMock(DataSource.class);
        SwiftMetaDataColumn metaDataColumn = EasyMock.createMock(SwiftMetaDataColumn.class);
        EasyMock.expect(parent1.getMetadata()).andReturn(metaData1).anyTimes();
        EasyMock.expect(parent2.getMetadata()).andReturn(metaData2).anyTimes();
        EasyMock.expect(metaData1.getColumn("column1")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData1.getColumn("column2")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData1.getColumn("column3")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData1.getColumn("column4")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData2.getColumn("column1")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData2.getColumn("column2")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData2.getColumn("column3")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaData2.getColumn("column4")).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaDataColumn.getName()).andReturn("column1").times(1).andReturn("column2").times(1).andReturn("column3").times(1).andReturn("column4").times(1);
        EasyMock.expect(metaDataColumn.getType()).andReturn(16).anyTimes();
        EasyMock.expect(metaDataColumn.getPrecision()).andReturn(8).anyTimes();
        EasyMock.expect(metaDataColumn.getScale()).andReturn(0).anyTimes();
        EasyMock.replay(parent1);
        EasyMock.replay(parent2);
        EasyMock.replay(metaData1);
        EasyMock.replay(metaData2);
        EasyMock.replay(metaDataColumn);
        List<DataSource> parents = new ArrayList<DataSource>();
        parents.add(parent1);
        parents.add(parent2);
        List<String> left = new ArrayList<String>();
        left.add("column1");
        left.add("column2");
        List<String> right = new ArrayList<String>();
        right.add("column3");
        right.add("column4");
        JoinColumn join1 = new JoinColumn("column1", true, "column1");
        JoinColumn join2 = new JoinColumn("column2", true, "column2");
        JoinColumn join3 = new JoinColumn("column3", false, "column3");
        JoinColumn join4 = new JoinColumn("column4", false, "column4");
        joinList.add(join1);
        joinList.add(join2);
        joinList.add(join3);
        joinList.add(join4);
        List<Segment[]> listOfSegments = new ArrayList<Segment[]>();
        listOfSegments.add(lSegment);
        listOfSegments.add(rSegment);
    }

    public void testLeftJoin() {
        String[][] str = new String[][]{{"A", "A", "A", "A"}, {"A", "A", "A", "A"}, {"A", "A", "A", "A"}, {"A", "A", "A", "A"}, {"A", "A", "A", "A"}, {"A", "A", "A", "A"},
                {"A", "A", "A", "A"}, {"A", "A", "A", "A"}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null},
                {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null},
                {"C", "C", null, null}, {"C", "C", null, null}};
        JoinOperatorResultSet rs = new JoinOperatorResultSet(joinList, lColumnKey, null, rColumnKey, lSegment, rSegment, false, false);
        try {
            int index = 0;
            while (rs.next()) {
                Row row = rs.getRowData();
                for (int i = 0; i < 4; i++) {
                    assertEquals(row.getValue(i), str[index][i]);
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testInnerJoin() {
        String[][] str = new String[][]{{"A", "A", null, null}, {"A", "A", null, null}, {"A", "A", null, null},
                {"A", "A", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}
                , {"B", "B", null, null}, {"B", "B", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}
        };
        JoinOperatorResultSet rs = new JoinOperatorResultSet(joinList, lColumnKey, null, rColumnKey, lSegment, rSegment, true, false);
        try {
            int index = 0;
            while (rs.next()) {
                Row row = rs.getRowData();
                for (int i = 0; i < 4; i++) {
                    assertEquals(row.getValue(i), str[index][i]);
                }
                index++;
            }
        } catch (Exception e) {
        }
    }

    public void testRightJoin() {
        String[][] str = new String[][]{{null, null, "1", "1"}, {null, null, "1", "1"}, {"A", "A", null, null}, {"A", "A", null, null}, {"A", "A", null, null},
                {"A", "A", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}, {"B", "B", null, null}
                , {"B", "B", null, null}, {"B", "B", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}, {"C", "C", null, null}
                , {null, null, "E", "E"}, {null, null, "A", "A"}, {null, null, "E", "E"}, {null, null, "A", "A"}};
        JoinOperatorResultSet rs = new JoinOperatorResultSet(joinList, lColumnKey, null, rColumnKey, lSegment, rSegment, true, true);
        try {
            int index = 0;
            while (rs.next()) {
                Row row = rs.getRowData();
                for (int i = 0; i < 4; i++) {
                    assertEquals(row.getValue(i), str[index][i]);
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testOutterJoin() {
        String[][] str = new String[][]{{null, null, "1", "1"}, {null, null, "1", "1"}, {null, null, "E", "E"}, {null, null, "A", "A"}, {null, null, "E", "E"}, {null, null, "A", "A"}};
        JoinOperatorResultSet rs = new JoinOperatorResultSet(joinList, lColumnKey, null, rColumnKey, lSegment, rSegment, false, true);
        try {
            int index = 0;
            while (rs.next()) {
                Row row = rs.getRowData();
                for (int i = 0; i < 4; i++) {
                    assertEquals(row.getValue(i), str[index][i]);
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
