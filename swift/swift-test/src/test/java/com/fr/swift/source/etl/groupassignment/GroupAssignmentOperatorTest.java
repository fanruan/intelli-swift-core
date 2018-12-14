package com.fr.swift.source.etl.groupassignment;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.etl.BaseCreateSegmentTest;
import com.fr.swift.source.etl.group.GroupAssignmentTransferOperator;
import com.fr.swift.source.etl.group.SingleGroup;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/22 0022 14:16
 */
public class GroupAssignmentOperatorTest extends TestCase {
    public void testGroupAssignment() throws Exception{
        Segment[] segments = new Segment[2];
        segments[0] = new BaseCreateSegmentTest().getSegment();
        segments[1] = new BaseCreateSegmentTest().getSegment();
        List<Segment[]> list = new ArrayList<Segment[]>();
        list.add(segments);
        String column1 = "column1";
        String column2 = "column2";
        SingleGroup singleGroup1 = new SingleGroup();
        String name1 = "XO";
        List<String> list1 = new ArrayList<String>();
        list1.add("A");
        singleGroup1.setList(list1);
        singleGroup1.setName(name1);
        SingleGroup singleGroup2 = new SingleGroup();
        String name2 = "OO";
        List<String> list2 = new ArrayList<String>();
        list2.add("B");
        singleGroup2.setList(list2);
        singleGroup2.setName(name2);
        String otherName = "other";
        List<SingleGroup> group = new ArrayList<SingleGroup>();
        group.add(singleGroup1);
        group.add(singleGroup2);
        ColumnKey columnKey = new ColumnKey(column1);
        GroupAssignmentTransferOperator operator = new GroupAssignmentTransferOperator(otherName, columnKey, group);
        SwiftResultSet rs = operator.createResultSet(null, null, list);
        int index = 0;
        String[] str = new String[]{"XO", "OO", "other", "OO", "other", "OO", "XO", "other", "OO",
                "XO", "OO", "other", "OO", "other", "OO", "XO", "other", "OO"};
        while (rs.hasNext()) {
            Row row = rs.getNextRow();
            for (int i = 0; i < 1; i++) {
                assertEquals(row.getValue(i), str[index]);
            }
            index++;
        }
    }
}
