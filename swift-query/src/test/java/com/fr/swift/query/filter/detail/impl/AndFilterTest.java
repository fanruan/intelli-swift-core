package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.filter.detail.DetailFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lyon on 2019/1/4.
 */
public class AndFilterTest extends BaseFilterTest {

    public void test() {
        DetailFilter filter = new InFilter(Collections.<Object>singleton(2), intColumn);
        DetailFilter filter1 = new InFilter(Collections.<Object>singleton(3), intColumn);
        DetailFilter not = new AndFilter(Arrays.asList(filter, filter1));
        check(new ArrayList<Integer>(), not.createFilterIndex());

        Set values = new HashSet();
        values.add(2);
        values.add(3);
        filter = new InFilter(values, intColumn);
        not = new AndFilter(Arrays.asList(filter, filter1));
        check(getRows(), not.createFilterIndex());
    }

    private List<Integer> getRows() {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (intDetails.get(i) != null && intDetails.get(i) == 3) {
                rows.add(i);
            }
        }
        return rows;
    }
}