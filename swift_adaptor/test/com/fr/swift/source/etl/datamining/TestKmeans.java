package com.fr.swift.source.etl.datamining;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.rowcal.rank.CreateSegmentForRank;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/21 0021 11:05
 */
public class TestKmeans extends TestCase {
    public void testKmeans() {
        try {
            Segment[] lSegment = new Segment[2];
            lSegment[0] = new CreateSegmentForRank().getSegment();
            lSegment[1] = new CreateSegmentForRank().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(lSegment);
            String column1 = "column1";
            String column2 = "column2";
            String column3 = "column3";
            int cluster = 3;
            int iterations = 10000;
            boolean replaceMissing = true;
            boolean distanceFunction = true;
            String[] dimensions = new String[]{column1, column2, column3};
            KmeansTransferOperator operator = new KmeansTransferOperator(cluster, iterations,
                    replaceMissing, distanceFunction, dimensions);
            SwiftResultSet rs = operator.createResultSet(null, null, list);
            String[][] str = new String[][]{{"1.0","14.0","16.0"}, {"3.0","14.0","18.0"}, {"1.6666666666666667","13.0","11.0"},
                    {"null","null","null"}, {"null","null","null"}, {"null","null","null"}, {"null","null","null"}, {"null","null","null"},
                    {"null","null","null"}, {"null","null","null"}, {"null","null","null"}, {"null","null","null"}, {"null","null","null"},
                    {"null","null","null"}};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 3; i ++) {
                    assertEquals(row.getValue(i) + "", str[index][i]);
                }
                index ++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
