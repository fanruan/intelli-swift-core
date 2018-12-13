package com.fr.swift.source.etl.columnfilter;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentTest;
import edu.emory.mathcs.backport.java.util.Arrays;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/24 0024 16:46
 */
public class ColumnFilterOperatorTest extends TestCase {

    public void testColumnFilter() throws Exception {
        IMocksControl control = EasyMock.createControl();
        FilterInfo filterInfo = control.createMock(FilterInfo.class);
        SwiftMetaData metaData = control.createMock(SwiftMetaData.class);
        SwiftMetaDataColumn metaDataColumn = control.createMock(SwiftMetaDataColumn.class);
        DetailFilter detailFilter = control.createMock(DetailFilter.class);
        EasyMock.expect(metaData.getColumnCount()).andReturn(1).anyTimes();
        EasyMock.expect(metaData.getColumn(EasyMock.anyInt())).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaDataColumn.getName()).andReturn("column1").anyTimes();
        EasyMock.expect(filterInfo.createDetailFilter(EasyMock.anyObject())).andReturn(detailFilter).anyTimes();
        MutableBitMap bitMap = RoaringMutableBitMap.of();
        bitMap.add(0);
        EasyMock.expect(detailFilter.createFilterIndex()).andReturn(bitMap).anyTimes();
        control.replay();
        Segment[] segment = new Segment[2];
        segment[0] = new BaseCreateSegmentTest().getSegment();
        segment[1] = new BaseCreateSegmentTest().getSegment();
        List<Segment[]> list = new ArrayList<Segment[]>();
        list.add(segment);
        ColumnFilterTransferOperator operator = new ColumnFilterTransferOperator(filterInfo);
        SwiftResultSet rs = operator.createResultSet(metaData, Arrays.asList(new SwiftMetaData[]{metaData}), list);
        int rowCount = 0;
        while (rs.hasNext()) {
            Row row = rs.getNextRow();
            assertEquals(row.getValue(0), "A");
            rowCount++;
        }
        assertEquals(rowCount, 2);

    }
}
