package com.fr.swift.source.etl.rowcal.period;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.rowcal.correspondperiod.PeriodTransferOperator;
import com.fr.swift.source.etl.rowcal.periodpercentage.CreateSegmentForPeriod;
import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/5 0006 17:05
 */
public class TestPeriodOperator extends TestCase {
    public void testPeriodOperator() {
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
            ColumnKey[] dimension = new ColumnKey[]{new ColumnKey(column1), new ColumnKey(column4)};
            PeriodTransferOperator operator = new PeriodTransferOperator(new ColumnKey(column1), new ColumnKey(column3), dimension);
            SwiftResultSet rs = operator.createResultSet(null, null, list);
            String[] str = new String[] {"null","1.0","null","1.0","null","1.0","null","1.0","null","1.0","null","1.0","null","null"};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 1; i ++) {
                    assertEquals(row.getValue(i) + "", str[index ++]);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
