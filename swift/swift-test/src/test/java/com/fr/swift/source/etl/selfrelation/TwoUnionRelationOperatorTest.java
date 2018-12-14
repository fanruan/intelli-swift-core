package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Handsome on 2018/1/19 0019 10:52
 */
public class TwoUnionRelationOperatorTest extends TestCase {

    public void testTwoUnionRelation() throws Exception {
        LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
        columns.put("层级1", new Integer(1));
        String idColumnName = "mmm";
        List<String> showColumns = new ArrayList<String>();
        showColumns.add("ID");
        showColumns.add("NAME");
        showColumns.add("region");
        showColumns.add("mmm");
        showColumns.add("ddd");
        showColumns.add("WWW");
        String parentIdColumnName = "ddd";
        SwiftMetaData metaData = EasyMock.createMock(SwiftMetaData.class);
        EasyMock.expect(metaData.getColumnCount()).andReturn(7).anyTimes();
        EasyMock.expect(metaData.getColumnName(1)).andReturn("ID").anyTimes();
        EasyMock.expect(metaData.getColumnName(2)).andReturn("NAME").anyTimes();
        EasyMock.expect(metaData.getColumnName(3)).andReturn("VALUE").anyTimes();
        EasyMock.expect(metaData.getColumnName(4)).andReturn("region").anyTimes();
        EasyMock.expect(metaData.getColumnName(5)).andReturn("mmm").anyTimes();
        EasyMock.expect(metaData.getColumnName(6)).andReturn("ddd").anyTimes();
        EasyMock.expect(metaData.getColumnName(7)).andReturn("WWW").anyTimes();
        EasyMock.expect(metaData.getColumnType(EasyMock.anyInt())).andReturn(1).anyTimes();
        EasyMock.replay(metaData);
        Segment[] segment = new Segment[1];
        segment[0] = new BaseCreateSegmentForSelfRelationTest().getSegment();
        //segment[1] = new BaseCreateSegmentForSelfRelationTest().getSegment();
        List<Segment[]> list = new ArrayList<Segment[]>();
        list.add(segment);
        String[][] str = new String[][]{{"109", "mmm", "江西", "11", "11", "aa"},
                {"102", "AAA", "江苏", "22", "22", "bb"},
                {"103", "AAA", "河南", "33", "33", ""},
                {"105", "fff", "湖北", "1", "1", ""},
                {"104", "mmm", "四川", "6", "6", null},
                {"107", "bbb", "", "", "", ""},
                {"107", "bbb", "", "", "", ""},
                {"104", "mmm", "四川", "6", "6", null},
                {"107", "bbb", "", "", "", ""}};
        String[] addNames = new String[]{"A", "B", "C", "D", "E", "F"};
        TwoUnionRelationTransferOperator operator = new TwoUnionRelationTransferOperator(columns, idColumnName, showColumns, addNames, parentIdColumnName);
        SwiftResultSet rs = operator.createResultSet(metaData, null, list);
        int k = -1;
        while (rs.hasNext()) {
            k++;
            Row row = rs.getNextRow();
            for (int i = 0; i < 6; i++) {
                assertEquals(row.getValue(i), str[k][i]);
            }
            if (k == 8) {
                //k = -1;
            }
        }
    }
}
