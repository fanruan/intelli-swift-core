package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2017/12/1.
 */
public class InFilterTest extends BaseFilterTest {

    public void test() {
        // int
        Set values = getValues(10, intDetails);
        List<Integer> expected = getExpected(values, intDetails);
        DetailFilter filter = new InFilter(values, intColumn);
        check(expected, filter.createFilterIndex());

        // double
        values = getValues(10, doubleDetails);
        expected = getExpected(values, doubleDetails);
        filter = new InFilter(values, doubleColumn);
        check(expected, filter.createFilterIndex());

        // long
        values = getValues(10, longDetails);
        expected = getExpected(values, longDetails);
        filter = new InFilter(values, longColumn);
        check(expected, filter.createFilterIndex());

        // string
        values = getValues(10, strDetails);
        expected = getExpected(values, strDetails);
        filter = new InFilter(values, strColumn);
        check(expected, filter.createFilterIndex());
    }

    public void testMatch() {
        // int
        Set values = getValues(10, intDetails);
        DetailFilter filter = new InFilter(values, intColumn);
        List<SwiftNode> nodes = new ArrayList<SwiftNode>();
        for (Object data : values) {
            nodes.add(new GroupNode(0, data));
        }
        for (SwiftNode node : nodes) {
            assertTrue(filter.matches(node, -1, null));
        }
        SwiftNode node = new GroupNode(0, 1000000);
        assertFalse(filter.matches(node, -1, null));
    }

    private Set getValues(int count, List detail) {
        Set set = new HashSet();
        for (int i = 0; i < count; i++) {
            set.add(detail.get(random.nextInt(rowCount)));
        }
        return set;
    }

    private List<Integer> getExpected(Set values, List detail) {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < detail.size(); i++) {
            if (values.contains(detail.get(i))) {
                rows.add(i);
            }
        }
        return rows;
    }
}
