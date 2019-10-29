package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringStartsWithFilterTest extends BaseFilterTest {

    public void test() {
        String start = chooseStartWith();
        DetailFilter filter = new StringStartsWithFilter(start, strColumn);
        check(getRows(start), filter.createFilterIndex());
    }

    private List<Integer> getRows(String start) {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (strDetails.get(i) != null && strDetails.get(i).startsWith(start)) {
                rows.add(i);
            }
        }
        return rows;
    }

    private String chooseStartWith() {
        String word = null;
        while (word == null || word.isEmpty()) {
            word = strDetails.get(random.nextInt(rowCount));
        }
        return word.substring(0, 1);
    }
}
