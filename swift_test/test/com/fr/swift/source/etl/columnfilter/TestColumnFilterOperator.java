package com.fr.swift.source.etl.columnfilter;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.CreateSegment;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/24 0024 16:46
 */
public class TestColumnFilterOperator extends TestCase {

    public void testColumnFilter () {
        try {
            FilterInfo filterInfo = EasyMock.createMock(FilterInfo.class);
            SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
            SwiftMetaDataColumn metaDataColumn = EasyMock.createMock(SwiftMetaDataColumn.class);
            EasyMock.expect(metaData.getColumnCount()).andReturn(2).anyTimes();
            EasyMock.expect(metaData.getColumn(EasyMock.anyInt())).andReturn(metaDataColumn).anyTimes();
            EasyMock.expect(metaDataColumn.getName()).andReturn("column1").times(1).andReturn("column2").times(1);
            EasyMock.replay(filterInfo);
            EasyMock.replay(metaData);
            EasyMock.replay(metaDataColumn);
            Segment[] segment = new Segment[2];
            segment[0] = new CreateSegment().getSegment();
            segment[1] = new CreateSegment().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(segment);
            ColumnFilterTransferOperator operator = new ColumnFilterTransferOperator(filterInfo);
            SwiftResultSet rs = operator.createResultSet(metaData, null, list);
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 2; i++) {
                    System.out.print(row.getValue(i) + "、");
                }
                System.out.println();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


    }
}
