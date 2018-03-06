package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.impl.string.not.StringNotStartsWithFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringNotStartsWithFilterTest extends StringStartsWithFilterTest {

    public StringNotStartsWithFilterTest() {
        this.filter = new StringNotStartsWithFilter(startsWith, details.size(), column);
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> !details.get(i).startsWith(startsWith))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}
