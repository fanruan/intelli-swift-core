package com.fr.swift.source.etl.columnformula;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.formula.ColumnFormulaTransferOperator;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:24
 */
public class TestColumnFormula extends TestCase {
    public void testColumnFormula() {
        try {
            Segment[] segments = new Segment[2];
            segments[0] = new CreateSegment().getSegment();
            segments[1] = new CreateSegment().getSegment();
            List<Segment[]> basedSegment = new ArrayList<Segment[]>();
            basedSegment.add(segments);
            String expression = "${column2} + ${column2}";
            ColumnType columnType = ColumnType.STRING;
            ColumnFormulaTransferOperator operator = new ColumnFormulaTransferOperator(columnType, expression);
            SwiftResultSet rs = operator.createResultSet(null, null, basedSegment);
            Object[][] str = new Object[][]{{"A",5,10},{"B",1,2},{"C",2,4},{"B",2,4},{"C",2,4},{"B",1,2},{"A",1,2},{"C",5,10},{"B",2,4}};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 3; i++) {
                    assertEquals(row.getValue(i).toString(), str[index][i].toString());
                }
                index ++;
                if(index == 9) {
                    index = 0;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
