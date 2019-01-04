package com.fr.swift.source.etl.union;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentForUnion1Test;
import com.fr.swift.source.etl.BaseCreateSegmentForUnion2Test;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 11:18
 */
public class UnionOperatorTest extends TestCase {

    public void testUnionOperator() {
        try {
            Segment[] lSegment = new Segment[2];
            Segment[] rSegment = new Segment[2];
            lSegment[0] = new BaseCreateSegmentForUnion1Test().getSegment();
            lSegment[1] = new BaseCreateSegmentForUnion1Test().getSegment();
            rSegment[0] = new BaseCreateSegmentForUnion2Test().getSegment();
            rSegment[1] = new BaseCreateSegmentForUnion2Test().getSegment();
            List<Segment[]> list = new ArrayList<Segment[]>();
            list.add(lSegment);
            list.add(rSegment);
            List<String> list1 = new ArrayList<String>();
            List<String> list2 = new ArrayList<String>();
            List<String> list3 = new ArrayList<String>();
            List<String> list4 = new ArrayList<String>();
            list1.add("column1/column3");
            list1.add("column1");
            list1.add("column3");
            list2.add("column2");
            list2.add("column2");
            list2.add("");
            list4.add("column4");
            list4.add("");
            list4.add("column4");
            List<List<String>> listString = new ArrayList<List<String>>();
            listString.add(list1);
            listString.add(list2);
            listString.add(list4);
            DataSource parent1 = EasyMock.createMock(DataSource.class);
            DataSource parent2 = parent1;
            SwiftMetaData metaData1 = EasyMock.createMock(SwiftMetaData.class);
            SwiftMetaDataColumn column1 = EasyMock.createMock(SwiftMetaDataColumn.class);
            EasyMock.expect(metaData1.getColumnType(EasyMock.anyInt())).andReturn(16).anyTimes();
            EasyMock.expect(parent1.getMetadata()).andReturn(metaData1).anyTimes();
            EasyMock.expect(metaData1.getColumn(EasyMock.isA(String.class))).andReturn(column1).anyTimes();
            EasyMock.expect(column1.getType()).andReturn(16).anyTimes();
            EasyMock.expect(column1.getScale()).andReturn(0).anyTimes();
            EasyMock.expect(column1.getPrecision()).andReturn(255).anyTimes();
            EasyMock.replay(parent1);
            EasyMock.replay(metaData1);
            EasyMock.replay(column1);
            SwiftMetaData[] metaDatas = new SwiftMetaData[2];
            metaDatas[0] = parent1.getMetadata();
            metaDatas[1] = parent2.getMetadata();
            UnionOperator operator = new UnionOperator(listString);
            List<SwiftMetaDataColumn> columnList = operator.getColumns(metaDatas);
            SwiftMetaData metaData = new SwiftMetaDataBean("aaa", columnList);
            UnionTransferOperator transOperator = new UnionTransferOperator(listString);
            SwiftResultSet rs = transOperator.createResultSet(metaData, null, list);
            String[][] str = new String[][]{{"A", "A", null}, {"B", "B", null}, {"C", "C", null}, {"B", "B", null}, {"C", "C", null}, {"B", "B", null},
                    {"A", "A", null}, {"C", "C", null}, {"B", "B", null}, {"A", "A", null}, {"B", "B", null}, {"C", "C", null}, {"B", "B", null}, {"C", "C", null},
                    {"B", "B", null}, {"A", "A", null}, {"C", "C", null}, {"B", "B", null}, {"A", null, "A"}, {"1", null, "1"}, {"E", null, "E"}, {"A", null, "A"},
                    {"1", null, "1"}};
            int k = 0;
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                for (int i = 0; i < 3; i++) {
                    assertEquals(str[k][i], row.getValue(i));
                }
                k++;
            }
        } catch (Exception e) {
        }
    }
}
