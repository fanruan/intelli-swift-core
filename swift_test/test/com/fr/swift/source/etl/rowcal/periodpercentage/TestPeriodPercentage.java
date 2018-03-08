package com.fr.swift.source.etl.rowcal.periodpercentage;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.rowcal.correspondperiodpercentage.CorrespondMonthPercentTransferOperator;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/5 0006 15:06
 */
public class TestPeriodPercentage extends TestCase {
    public void testPeriodPercentage() {
        try {
            Segment[] segments = new Segment[2];
            segments[0] = new CreateSegmentForPeriod().getSegment();
            segments[1] = new CreateSegmentForPeriod().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(segments);
            String column1 = "column1";
            String column2 = "column2";
            String column3 = "column3";
            String column4 = "column4";
            ColumnKey[] dimension = new ColumnKey[]{new ColumnKey(column1)};
            CorrespondMonthPercentTransferOperator operator = new CorrespondMonthPercentTransferOperator(new ColumnKey(column1), new ColumnKey(column3), dimension);
            SwiftResultSet rs = operator.createResultSet(null, null, list);
            String[] str = new String[]{"Infinity","Infinity","Infinity","0.0","0.0","0.0","Infinity","Infinity","Infinity","0.0","0.0","0.0","Infinity","Infinity"};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 1; i ++) {
                    assertEquals(row.getValue(i).toString(), str[index ++]);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();;
        }
    }
}
