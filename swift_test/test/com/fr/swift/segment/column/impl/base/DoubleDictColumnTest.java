package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.stream.Stream;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class DoubleDictColumnTest extends BaseDictColumnTest<Double> {
    int size = 10000;

    {
        c = Double::compareTo;
        values = Stream.generate(() -> r.nextDouble()).limit(size).distinct().sorted(c).toArray(Double[]::new);
    }

    @Override
    DictionaryEncodedColumn<Double> getDictColumn() {
        return new DoubleDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_double"),
                c);
    }

}