package com.fr.swift.source.etl.rowcal.rank;

import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 11:48
 */
public class RankRowOperatorTest extends TestCase {

    public void testRankRowOperator() throws SQLException {
        Segment[] segments = new Segment[2];
        segments[0] = new CreateSegmentForRank().getSegment();
        segments[1] = new CreateSegmentForRank().getSegment();
        List<Segment[]> list = new ArrayList<Segment[]>();
        list.add(segments);
        String column1 = "column1";
        String column2 = "column2";
        String column3 = "column3";
        ColumnKey[] dimension = new ColumnKey[]{new ColumnKey(column1), new ColumnKey(column2)};
        RankRowTransferOperator operator = new RankRowTransferOperator(SortType.ASC, new ColumnKey(column3), dimension);
        SwiftResultSet rs = operator.createResultSet(null, null, list);
        String[] str = new String[]{"1.0", "1.0", "3.0", "5.0", "1.0", "3.0", "1.0", "1.0", "1.0", "3.0", "5.0", "1.0", "3.0", "1.0"};
        int index = 0;
        while (rs.hasNext()) {
            Row row = rs.getNextRow();
            for (int i = 0; i < 1; i++) {
                assertEquals(Double.valueOf(row.getValue(i).toString()).toString(), str[index++]);
            }
        }
    }
}
