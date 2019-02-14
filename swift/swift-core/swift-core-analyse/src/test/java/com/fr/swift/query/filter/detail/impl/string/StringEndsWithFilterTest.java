package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringEndsWithFilterTest extends BaseFilterTest {

    public void test() {
        String end = chooseStartWith();
        DetailFilter filter = new StringEndsWithFilter(end, strColumn);
        check(getRows(end), filter.createFilterIndex());
    }

    private List<Integer> getRows(String start) {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (strDetails.get(i) != null && strDetails.get(i).endsWith(start)) {
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
        return word.substring(word.length() - 1);
    }
}
