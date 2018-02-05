package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Handsome on 2018/1/19 0019 10:52
 */
public class TestTwoUnionRelationOperator extends TestCase {

    public void testTwoUnionRealtion() {
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
        int columnType = 0;
        String columnName = null;
        String parentIdColumnName = "ddd";
        try {
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
            segment[0] = new CreateSegmentForSelfRelation().getSegment();
            //segment[1] = new CreateSegmentForSelfRelation().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(segment);
            String[][] str = new String[][]{{"109","mmm","123","江西","11","11","aa","109","mmm","江西","11","11","aa"},
                    {"102","AAA","123","江苏","22","22","bb","102","AAA","江苏","22","22","bb"},
                    {"103","AAA","123","河南","33","33","","103","AAA","河南","33","33",""},
                    {"105","fff","333","湖北","1","1","","105","fff","湖北","1","1",""},
                    {"106","ggg","321","福建","6","6","","104","mmm","四川","6","6",null},
                    {"106","nnn","","","","",null,"107","bbb","","","",""},
                    {"101","ggg","323","安徽","","","","107","bbb","","","",""},
                    {"104","mmm","777","四川","6","6",null,"104","mmm","四川","6","6",null},
                    {"107","bbb","","","","","","107","bbb","","","",""}};
            TwoUnionRelationTransferOperator operator = new TwoUnionRelationTransferOperator(columns, idColumnName, showColumns, columnType, columnName, parentIdColumnName);
            SwiftResultSet rs = operator.createResultSet(metaData, null, list);
            int k = -1;
            while(rs.next()) {
                k++;
                Row row = rs.getRowData();
                for(int i = 0; i < 13; i++) {
                    assertEquals(row.getValue(i),str[k][i]);
                }
                if(k == 8){
                    //k = -1;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
