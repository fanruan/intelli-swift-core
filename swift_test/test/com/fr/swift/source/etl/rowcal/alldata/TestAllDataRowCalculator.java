package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.rowcal.rank.CreateSegmentForRank;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 16:38
 */
public class TestAllDataRowCalculator extends TestCase {
    public void testAllDataRow() {
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
            AggregatorType[] summaryType = new AggregatorType[]{AggregatorType.SUM, AggregatorType.COUNT,
                    AggregatorType.MIN, AggregatorType.MAX, AggregatorType.AVERAGE};
            int columnType = 0;
            String[] str = new String[]{"20.0", "20.0", "96.0", "96.0", "96.0", "96.0", "96.0", "96.0", "46.0", "46.0", "46.0", "46.0", "36.0", "36.0"};
            int index = 0;
            for(int k = 0; k < 1; k++) {
                AllDataTransferOperator operator = new AllDataTransferOperator(summaryType[k], column3, dimension);
                SwiftResultSet rs = operator.createResultSet(null, null, list);
                while(rs.next()) {
                    Row row = rs.getRowData();
                    for(int i = 0; i < 1; i++) {
                        assertEquals(row.getValue(i).toString(), str[index ++]);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
