package com.fr.swift.query.filter.detail.impl.date;

import com.fr.swift.query.filter.detail.impl.ColumnImplTest;
import com.fr.swift.query.filter.detail.impl.string.BaseStringFilterTest;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lyon on 2017/12/5.
 */
public abstract class BaseDateFilterTest extends BaseStringFilterTest {

    protected static List<Long> dates = prepare();

    protected Comparator<Long> comparator = Comparator.naturalOrder();
    protected List<Long> details;

    public BaseDateFilterTest() {
        this.details = dates;
        this.column = new ColumnImplTest<Long>(details, comparator, null) {
            @Override
            protected Long convertValue(Object value) {
                return ((Number) value).longValue();
            }
        };
    }

    public static List<Long> prepare() {
        long today = new Date().getTime();
        long day = 24*60*60*1000;
        List<Long> groups = IntStream.range(0, 200).mapToLong(i -> day * i + today)
                .mapToObj(Long::new).collect(Collectors.toList());
        return IntStream.range(0, 500).mapToObj(i -> groups.get(random.nextInt(groups.size())))
                .collect(Collectors.toList());
    }
}
