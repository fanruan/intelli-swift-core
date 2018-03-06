package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.stream.Stream;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class StringDictColumnTest extends BaseDictColumnTest<String> {

    int size = 10000;

    {
        c = String::compareTo;
        byte[] bytes = new byte[100];
        values = Stream.generate(() -> {
            r.nextBytes(bytes);
            return new String(bytes);
        }).limit(size).distinct().sorted(c).toArray(String[]::new);
    }

    @Override
    DictionaryEncodedColumn<String> getDictColumn() {
        return new StringDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_string"),
                c);
    }

}
