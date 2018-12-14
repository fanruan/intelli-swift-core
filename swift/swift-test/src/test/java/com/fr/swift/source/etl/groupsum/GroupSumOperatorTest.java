package com.fr.swift.source.etl.groupsum;

import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.NoGroupRule;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateSegmentForSumTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 16:05
 */
public class GroupSumOperatorTest extends TestCase {


    public void testSumByGroup() throws Exception {
        SwiftResultSet rs = init(AggregatorType.SUM);
        Object[][] value = new Object[][]{{"A", new Double(12.0)}, {"B", new Double(12.0)}, {"C", new Double(18.0)}};
        int index = 0;
        while (rs.hasNext()) {
            Row row = rs.getNextRow();
            for (int i = 0; i < 2; i++) {
                assertEquals(row.getValue(i), value[index][i]);
            }
            index++;
        }
    }

    public SwiftResultSet init(AggregatorType type) {
        GroupSumTarget[] target = new GroupSumTarget[1];
        GroupSumDimension[] dimension = new GroupSumDimension[1];
        target[0] = new GroupSumTarget();
        target[0].setName("column2");
        target[0].setAggregator(AggregatorFactory.createAggregator(AggregatorType.SUM));
        // TODO   应该是整型
        dimension[0] = new GroupSumDimension();
        dimension[0].setName("column1");
        dimension[0].setGroup(Groups.newGroup(new NoGroupRule()));
        Segment[] segment = new Segment[2];
        segment[0] = new BaseCreateSegmentForSumTest().getSegment();
        segment[1] = new BaseCreateSegmentForSumTest().getSegment();
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
            GroupSumTransferOperator operator = new GroupSumTransferOperator(target, dimension);
            SwiftResultSet rs = operator.createResultSet(null, null, listOfSegment);
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
