package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.datamining.createsegment.CreateSegment;
import com.fr.swift.source.etl.datamining.kmeans.KmeansResultSet;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/21 0021 11:05
 */
public class TestKmeans extends TestCase {
    public void testKmeans() {
        try {
            Segment[] segment = new Segment[2];
            segment[0] = new CreateSegment().getSegment();
            segment[1] = new CreateSegment().getSegment();
            String column1 = "column1";
            String column2 = "column2";
            int cluster = 3;
            int iterations = 10000;
            boolean replaceMissing = true;
            boolean distanceFunction = true;
            String clusterName = "聚类";
            String[] dimensions = new String[]{column1, column2};
            KmeansBean bean = new KmeansBean(cluster, iterations, replaceMissing, distanceFunction, dimensions, clusterName);
            SwiftResultSet rs = new KmeansResultSet(bean, null, segment);
            String[][] str = new String[][]{{"6.0","1.0","21.666666666666668"},{"4.0","2.0","18.5"},{"6.0","1.0","19.0"}};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 3; i ++) {
                    if(index < cluster) {
                        assertEquals(str[index][i], row.getValue(i).toString());
                    } else {
                        assertEquals(row.getValue(i), null);
                    }
                }
                index ++;
                System.out.println();
            }
            /*SwiftResultSet rs = operator.createResultSet(null, null, list);
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
            }*/
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
