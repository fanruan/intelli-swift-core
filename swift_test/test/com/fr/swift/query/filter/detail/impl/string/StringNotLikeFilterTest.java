package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.impl.string.not.StringNotLikeFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/11/30.
 */
public class StringNotLikeFilterTest extends StringLikeFilterTest {

    public StringNotLikeFilterTest() {
        this.filter = new StringNotLikeFilter(like, details.size(), column);
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> !details.get(i).contains(like))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}
