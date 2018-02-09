package com.fr.swift.source.etl.groupsum;

import com.fr.swift.query.group.Group;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.CreateSegmentForSum;
import com.fr.swift.source.etl.utils.ETLConstant;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 16:05
 */
public class TestSumByGroupOperator extends TestCase {


    public void testSumByGroup() {
        SwiftResultSet rs = init(ETLConstant.SUMMARY_TYPE.SUM);
        Object[][] value = new Object[][]{{"A", new Double(12.0)}, {"B", new Double(12.0)}, {"C", new Double(18.0)}};
        int index = 0;
        try {
            while(rs.next()) {
                Row row = rs.getRowData();
                for(int i = 0; i < 2; i++) {
                    //System.out.print(row.getValue(i)+"、");
                    assertEquals(row.getValue(i), value[index][i]);
                }
                System.out.println();
                index ++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public SwiftResultSet init(int type) {
        SumByGroupTarget[] target = new SumByGroupTarget[1];
        SumByGroupDimension[] dimension = new SumByGroupDimension[1];
        target[0] = new SumByGroupTarget();
        target[0].setSumType(type);
        target[0].setName("column2");
        target[0].setClassType(1);// TODO   应该是整型
        target[0].setColumnType(ColumnType.STRING);
        dimension[0] = new SumByGroupDimension();
        dimension[0].setName("column1");
        dimension[0].setClassType(16);
        dimension[0].setColumnType(16);

        Segment[] segment = new Segment[2];
        segment[0] = new CreateSegmentForSum().getSegment();
        segment[1] = new CreateSegmentForSum().getSegment();
        List<Segment[]> listOfSegment = new ArrayList<Segment[]>();
        listOfSegment.add(segment);
        try {
            SwiftMetaData parent = EasyMock.createMock(SwiftMetaData.class);
            Group group = EasyMock.createMock(Group.class);
            SwiftMetaDataColumn column1 = EasyMock.createMock(SwiftMetaDataColumn.class);
            EasyMock.expect(parent.getColumn("column1")).andReturn(column1).anyTimes();
            EasyMock.expect(column1.getType()).andReturn(16).anyTimes();
            EasyMock.expect(column1.getPrecision()).andReturn(10).anyTimes();
            // EasyMock.expect(group.getGroupType()).andReturn(14).anyTimes();
            EasyMock.replay(parent);
            EasyMock.replay(group);
            EasyMock.replay(column1);
            List list = new ArrayList();
            list.add(parent);
            SumByGroupTransferOperator operator = new SumByGroupTransferOperator(target, dimension);
            SwiftResultSet rs = operator.createResultSet(null, null, listOfSegment);
            return rs;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
