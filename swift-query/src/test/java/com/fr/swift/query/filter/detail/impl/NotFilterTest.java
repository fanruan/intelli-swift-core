package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.filter.detail.DetailFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lyon on 2019/1/4.
 */
public class NotFilterTest extends BaseFilterTest {

    public void test() {
        DetailFilter filter = new InFilter(Collections.<Object>singleton(2), intColumn);
        DetailFilter not = new NotFilter(rowCount, filter);
        check(getRows(), not.createFilterIndex());
    }

    private List<Integer> getRows() {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (intDetails.get(i) == null || intDetails.get(i) != 2) {
                rows.add(i);
            }
        }
        return rows;
    }
}