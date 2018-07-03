package com.fr.swift.query.filter.detail.impl.string;

import com.fr.swift.query.filter.detail.impl.InFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/11/29.
 */
public class StringInFilterTest extends BaseStringFilterTest {

    protected Set<Object> in;

    public StringInFilterTest() {
        init();
        this.filter = new InFilter(in, column);
    }

    private void init() {
        int len = random.nextInt(5) + 1;
        in = IntStream.range(0, len).mapToObj(i -> details.get(i)).collect(Collectors.toSet());
    }

    @Override
    protected List<Integer> getExpectedIndexes() {
        return IntStream.range(0, details.size()).filter(i -> in.contains(details.get(i)))
                .mapToObj(Integer::new).collect(Collectors.toList());
    }
}
