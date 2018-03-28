package com.fr.swift.source.etl.date;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.CreateSegmentForSum;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Handsome on 2018/3/3 0003 13:40
 */
public class TestGetFromDate extends TestCase {
    public void testGetDate() {
        try {
            Segment[] segments = new Segment[2];
            segments[0] = new CreateSegmentForSum().getSegment();
            segments[1] = new CreateSegmentForSum().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(segments);
            String column1 = "column2";
            int unit = 0x1;
            SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
            SwiftMetaDataColumn column = EasyMock.createMock(SwiftMetaDataColumn.class);
            EasyMock.expect(metaData.getColumn(column1)).andReturn(column).anyTimes();
            EasyMock.expect(column.getName()).andReturn(column1).anyTimes();
            EasyMock.expect(column.getType()).andReturn(Types.DATE).anyTimes();
            EasyMock.expect(column.getScale()).andReturn(10).anyTimes();
            EasyMock.expect(column.getPrecision()).andReturn(10).anyTimes();
            EasyMock.replay(metaData);
            EasyMock.replay(column);
            GetFromDateTransferOperator operator = new GetFromDateTransferOperator(column1, unit);
            SwiftResultSet rs = operator.createResultSet(metaData, null, list);
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 1; i ++) {
                    assertEquals(row.getValue(i) + "", "1970");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
