package com.fr.swift.segment.column.impl.base;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import org.junit.Before;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class StringDictColumnTest extends BaseDictColumnTest<String> {

    @Before
    public void setUp() throws Exception {
        c = Comparators.asc();
        byte[] bytes = new byte[100];

        Set<String> strings = new TreeSet<String>(c);
        while (strings.size() < size) {
            r.nextBytes(bytes);
            strings.add(new String(bytes));
        }
        values = strings.toArray(new String[]{});
    }

    @Override
    DictionaryEncodedColumn<String> getDictColumn() {
        return new StringDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_string"),
                c);
    }

}
