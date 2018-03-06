package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.impl.string.not.StringNotInFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringNotInFilterTest extends StringInFilterTest {

    public StringNotInFilterTest() {
        this.filter = new StringNotInFilter(in, details.size(), column);
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> !in.contains(details.get(i)))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}
