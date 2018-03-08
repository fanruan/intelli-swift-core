package com.fr.swift.source.etl.columnrowtrans;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.CreateSegmentForColumnTrans;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/23 0023 15:11
 */
public class TestColumnRowTrans extends TestCase {

    public void testColumnRowTrans() {
        List<String> otherColumns = new ArrayList<String>();
        otherColumns.add("column2");
        ColumnKey key1 = new ColumnKey("column1");
        ColumnKey key2 = new ColumnKey("column2");
        Segment segment1 = new CreateSegmentForColumnTrans().getSegment();
        Segment segment2 = new CreateSegmentForColumnTrans().getSegment();
        NameText nameText = new NameText("column2", "");
        NameText lc_value1 = new NameText("A", "");
        NameText lc_value2 = new NameText("B", "");
        NameText lc_value3 = new NameText("C", "");
        List<NameText> column = new ArrayList<NameText>();
        List<NameText> lc_value = new ArrayList<NameText>();
        lc_value.add(lc_value1);
        lc_value.add(lc_value2);
        lc_value.add(lc_value3);
        column.add(nameText);
        SwiftMetaData table = EasyMock.createMock(SwiftMetaData.class);
        SwiftMetaDataColumn singleColumn = EasyMock.createMock(SwiftMetaDataColumn.class);
        try {
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
            SwiftMetaDataImpl basicMeta = new SwiftMetaDataImpl("aaa", columnList);
            ColumnRowTransferOperator operator = new ColumnRowTransferOperator("column1", "column1", column, lc_value, transOperator.getOtherColumnNames());
            SwiftResultSet set = operator.createResultSet(basicMeta, null, segments);
            String[][] str = new String[][]{{"A","A","A",null,null},{"B","B",null,"B",null},{"C","C",null,null, "C"}};
            int j = 0;
            while (set.next()) {
                Row row = set.getRowData();
                for (int i = 0; i < 5; i++) {
                    assertEquals(row.getValue(i), str[j][i]);
                    //System.out.print(row.getValue(i)+"、");
                }
                //System.out.println();
                j++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
