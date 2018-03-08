package com.fr.swift.source.etl.datediff;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.CreateSegmentForSum;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 15:41
 */
public class TestDateDiffOperator extends TestCase{
    public void testDateDiff() {
        try {
            Segment[] segments = new Segment[2];
            segments[0] = new CreateSegmentForSum().getSegment();
            segments[1] = new CreateSegmentForSum().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(segments);
            String column1 = "column2";
            String column2 = "column2";
            int unit = 0x5;
            SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
            SwiftMetaDataColumn column = EasyMock.createMock(SwiftMetaDataColumn.class);
            EasyMock.expect(metaData.getColumn(column1)).andReturn(column).anyTimes();
            EasyMock.expect(column.getName()).andReturn(column1).anyTimes();
            EasyMock.expect(column.getType()).andReturn(91).anyTimes();
            EasyMock.replay(metaData);
            EasyMock.replay(column);
            DateDiffTransferOperator operator = new DateDiffTransferOperator(column1, column2, unit);
            SwiftResultSet rs = operator.createResultSet(metaData, null, list);
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 1; i ++) {
                    assertEquals(row.getValue(i) + "", "0");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
