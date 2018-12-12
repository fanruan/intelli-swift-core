package com.fr.swift.source.etl.columnformula;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.Row;
import com.fr.swift.source.etl.formula.ColumnFormulaTransferOperator;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:24
 */
public class ColumnFormulaOperatorTest extends TestCase {
    public void testColumnFormula() {
        try {
            Segment[] segments = new Segment[2];
            segments[0] = new BaseCreateSegmentTest().getSegment();
            segments[1] = new BaseCreateSegmentTest().getSegment();
            List<Segment[]> basedSegment = new ArrayList<Segment[]>();
            basedSegment.add(segments);
            String expression = "${column2} + ${column2}";
            ColumnType columnType = ColumnType.STRING;
            ColumnFormulaTransferOperator operator = new ColumnFormulaTransferOperator(columnType, expression);
            SwiftResultSet rs = operator.createResultSet(null, null, basedSegment);
            Object[] str = new Object[]{"10","2","4","4","4","2","2","10","4","10","2","4","4","4","2","2","10","4"};
            int index = 0;
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                for(int i = 0; i < 1; i++) {
                    assertEquals(row.getValue(i).toString(), str[index].toString());
                }
                index ++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
