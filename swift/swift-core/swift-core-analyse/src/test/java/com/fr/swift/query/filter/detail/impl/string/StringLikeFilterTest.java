package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringLikeFilterTest extends BaseFilterTest {

    public void test() {
        String like = chooseStartWith();
        DetailFilter filter = new StringLikeFilter(like, strColumn);
        check(getRows(like), filter.createFilterIndex());
    }

    private List<Integer> getRows(String like) {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            if (strDetails.get(i) != null && strDetails.get(i).contains(like)) {
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
        int start = random.nextInt(word.length());
        return word.substring(start, start + 1);
    }
}
