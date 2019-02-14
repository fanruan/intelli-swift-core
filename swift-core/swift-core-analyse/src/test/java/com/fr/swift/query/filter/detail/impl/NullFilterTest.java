package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.filter.detail.DetailFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public class NullFilterTest extends BaseFilterTest {

    public void test() {
        DetailFilter filter = new NullFilter(intColumn);
        check(getNullRows(intDetails), filter.createFilterIndex());

        filter = new NullFilter(doubleColumn);
        check(getNullRows(doubleDetails), filter.createFilterIndex());

        filter = new NullFilter(longColumn);
        check(getNullRows(longDetails), filter.createFilterIndex());

        filter = new NullFilter(strColumn);
        check(getNullRows(strDetails), filter.createFilterIndex());
    }

    private List<Integer> getNullRows(List detail) {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < detail.size(); i++) {
            if (detail.get(i) == null) {
                rows.add(i);
            }
        }
        return rows;
    }
}
