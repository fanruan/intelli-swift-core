package com.fr.swift.source.etl.datediff;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentForSumTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 15:41
 */
public class DateDiffOperatorTest extends TestCase {
    public void testDateDiff() throws Exception {
        SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
        Segment[] segments = new Segment[2];
        segments[0] = new BaseCreateSegmentForSumTest().getSegment(metaData);
        segments[1] = new BaseCreateSegmentForSumTest().getSegment(metaData);
        List<Segment[]> list = new ArrayList<Segment[]>();
        list.add(segments);
        String column1 = "column2";
        String column2 = "column2";
        SwiftMetaDataColumn column = EasyMock.createMock(SwiftMetaDataColumn.class);
        EasyMock.expect(metaData.getColumn(column1)).andReturn(column).anyTimes();
        EasyMock.expect(column.getName()).andReturn(column1).anyTimes();
        EasyMock.expect(column.getType()).andReturn(Types.DATE).anyTimes();
        EasyMock.expect(column.getScale()).andReturn(10).anyTimes();
        EasyMock.expect(column.getPrecision()).andReturn(0).anyTimes();
        EasyMock.replay(metaData);
        EasyMock.replay(column);
        DateDiffTransferOperator operator = new DateDiffTransferOperator(column1, column2, GroupType.YEAR);
        SwiftResultSet rs = operator.createResultSet(metaData, null, list);
        while (rs.hasNext()) {
            Row row = rs.getNextRow();
            for (int i = 0; i < 1; i++) {
                assertEquals(row.getValue(i) + "", "0");
            }
        }
    }
}
