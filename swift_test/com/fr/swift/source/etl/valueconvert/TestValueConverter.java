package com.fr.swift.source.etl.valueconvert;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.CreateSegmentForSum;
import com.fr.swift.source.etl.valueconverter.ValueConverterTransferOperator;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/23 0023 16:20
 */
public class TestValueConverter extends TestCase {
    public void testValueConverter() {
        try {
            Segment[] lSegment = new Segment[2];
            lSegment[0] = new CreateSegmentForSum().getSegment();
            lSegment[1] = new CreateSegmentForSum().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(lSegment);
            SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
            SwiftMetaDataColumn column = EasyMock.createMock(SwiftMetaDataColumn.class);
            EasyMock.expect(metaData.getColumn("column2")).andReturn(column).anyTimes();
            EasyMock.expect(column.getType()).andReturn(10000).anyTimes();
            EasyMock.replay(metaData);
            EasyMock.replay(column);
            ValueConverterTransferOperator operator = new ValueConverterTransferOperator("column2", 0);
            SwiftResultSet rs = operator.createResultSet(metaData, null, list);
            String[] str = new String[]{"5.0","1.0","2.0","2.0","2.0","1.0","1.0","5.0","2.0"};
            int index = 0;
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 1; i++) {
                    assertEquals(row.getValue(i).toString(), str[index]);
                }
                index ++;
                if(index == 9) {
                    index = 0;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
