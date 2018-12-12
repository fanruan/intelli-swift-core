package com.fr.swift.source.etl.columnrowtrans;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentForColumnTransTest;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/23 0023 15:11
 */
public class ColumnRowTransOperatorTest extends TestCase {

    public void testColumnRowTrans() throws Exception{
        List<Pair<String, String>> otherColumns = new ArrayList<Pair<String, String>>();
        otherColumns.add(Pair.of("column2", "column2"));
        ColumnKey key1 = new ColumnKey("column1");
        ColumnKey key2 = new ColumnKey("column2");
        Segment segment1 = new BaseCreateSegmentForColumnTransTest().getSegment();
        Segment segment2 = new BaseCreateSegmentForColumnTransTest().getSegment();
        Pair<String, String> nameText = new Pair<String, String>("column2", "");
        Pair<String, String> lc_value1 = new Pair<String, String>("A", "");
        Pair<String, String> lc_value2 = new Pair<String, String>("B", "");
        Pair<String, String> lc_value3 = new Pair<String, String>("C", "");
        List<Pair<String, String>> column = new ArrayList<Pair<String, String>>();
        List<Pair<String, String>> lc_value = new ArrayList<Pair<String, String>>();
        lc_value.add(lc_value1);
        lc_value.add(lc_value2);
        lc_value.add(lc_value3);
        column.add(nameText);
        SwiftMetaData table = EasyMock.createMock(SwiftMetaData.class);
        SwiftMetaDataColumn singleColumn = EasyMock.createMock(SwiftMetaDataColumn.class);
        EasyMock.expect(table.getColumn(EasyMock.isA(String.class))).andReturn(singleColumn).anyTimes();
        EasyMock.expect(singleColumn.getType()).andReturn(16).anyTimes();
        EasyMock.expect(singleColumn.getPrecision()).andReturn(16).anyTimes();
        EasyMock.expect(singleColumn.getScale()).andReturn(16).anyTimes();
        EasyMock.expect(table.getColumnCount()).andReturn(3).anyTimes();
        EasyMock.expect(table.getColumnName(1)).andReturn("column1").anyTimes();
        EasyMock.expect(table.getColumnName(2)).andReturn("column2").anyTimes();
        EasyMock.expect(table.getColumnName(3)).andReturn("column3").anyTimes();
        EasyMock.expect(singleColumn.getName()).andReturn("column1").times(1).andReturn("column2").times(1).andReturn("column3").times(1).andReturn("column3").times(1);
        EasyMock.replay(singleColumn);
        EasyMock.replay(table);
        SwiftMetaData[] tables = new SwiftMetaData[]{table};
        List<Segment[]> segments = new ArrayList<Segment[]>();
        Segment[] seg = new Segment[]{segment1, segment2};
        segments.add(seg);
        ColumnRowTransOperator transOperator = new ColumnRowTransOperator("column1", "column1", lc_value, column, otherColumns);
        List<SwiftMetaDataColumn> columnList = transOperator.getColumns(tables);
        SwiftMetaData basicMeta = new SwiftMetaDataBean("aaa", columnList);
        ColumnRowTransferOperator operator = new ColumnRowTransferOperator("column1", "column1", column, lc_value, transOperator.getOtherColumnNames());
        SwiftResultSet set = operator.createResultSet(basicMeta, null, segments);
        String[][] str = new String[][]{{"A", "A", null, null, "A"}, {"B", null, "B", null, "B"}, {"C", null, null, "C", "C"}};
        int j = 0;
        while (set.hasNext()) {
            Row row = set.getNextRow();
            for (int i = 0; i < 5; i++) {
                assertEquals(row.getValue(i), str[j][i]);
            }
            j++;
        }

    }
}
