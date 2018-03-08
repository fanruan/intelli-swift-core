package com.fr.swift.source.etl.rowcal.accumulate;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.rowcal.rank.CreateSegmentForRank;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 16:14
 */
public class TestAccumulateRowOperator extends TestCase {

    public void testAccumulateRowOperator() {
        try {
            Segment[] lSegment = new Segment[2];
            lSegment[0] = new CreateSegmentForRank().getSegment();
            lSegment[1] = new CreateSegmentForRank().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(lSegment);
            String column1 = "column1";
            String column2 = "column2";
            String column3 = "column3";
            ColumnKey[] dimension = new ColumnKey[]{new ColumnKey(column1), new ColumnKey(column2)};
            AccumulateRowTransferOperator operator = new AccumulateRowTransferOperator(new ColumnKey(column3), dimension);
            SwiftResultSet rs = operator.createResultSet(null, null, list);
            String[] str = new String[]{"10.0","20.0","15.0","31.0","48.0","63.0","79.0",
                    "96.0","11.0","23.0","34.0","46.0","18.0","36.0"};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 1; i ++) {
                    assertEquals(str[index++], row.getValue(i)+"");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
