package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentForColumnTransTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 11:08
 */
public class OneUnionRelationOperatorTest extends TestCase {
    public void testOneColumn() throws Exception {
        String columnName = null;
        int columnType = 0;
        LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
        columns.put("层级1", new Integer(0));
        columns.put("层级2", new Integer(1));
        columns.put("层级3", new Integer(2));
        String idColumnName = "column3";
        List<String> showColumns = new ArrayList<String>();
        showColumns.add("ID");
        SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
        SwiftMetaDataColumn metaDataColumn = EasyMock.createMock(SwiftMetaDataColumn.class);
        DataSource source = EasyMock.createMock(DataSource.class);
        EasyMock.expect(metaData.getColumnCount()).andReturn(3).anyTimes();
        EasyMock.expect(source.getMetadata()).andReturn(metaData).anyTimes();
        EasyMock.expect(metaData.getColumn(idColumnName)).andReturn(metaDataColumn).anyTimes();
        EasyMock.expect(metaDataColumn.getType()).andReturn(0x10).anyTimes();
        EasyMock.expect(metaData.getColumnName(1)).andReturn("column1").anyTimes();
        EasyMock.expect(metaData.getColumnName(2)).andReturn("column2").anyTimes();
        EasyMock.expect(metaData.getColumnName(3)).andReturn("column3").anyTimes();
        EasyMock.replay(source);
        EasyMock.replay(metaDataColumn);
        EasyMock.replay(metaData);
        Segment[] segments = new Segment[2];
        segments[0] = new BaseCreateSegmentForColumnTransTest().getSegment();
        segments[1] = new BaseCreateSegmentForColumnTransTest().getSegment();
        OneUnionRelationTransferOperator operator = new OneUnionRelationTransferOperator(columnName, showColumns, idColumnName, columnType, columns);
        List list = new ArrayList();
        list.add(segments);
        List parents = new ArrayList();
        parents.add(source);
        String[][] str = new String[][]{{null, null, null}, {null, null, null}, {null, null, null}, {null, null, null},
                {null, null, null}, {null, null, null}, {null, null, null}, {null, null, null}, {null, null, null}};
        SwiftResultSet rs = operator.createResultSet(metaData, parents, list);
        int k = 0;
        while (rs.hasNext()) {
            Row row = rs.getNextRow();
            for (int i = 0; i < 3; i++) {
                assertEquals(str[k][i], row.getValue(i));
            }
            k++;
            if (k > 8) {
                k = 0;
            }
        }

    }
}
