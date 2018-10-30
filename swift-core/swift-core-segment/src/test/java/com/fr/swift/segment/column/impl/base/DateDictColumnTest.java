package com.fr.swift.segment.column.impl.base;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import org.junit.Before;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author anchore
 * @date 2018/10/30
 */
public class DateDictColumnTest extends BaseDictColumnTest<Date> {

    @Before
    public void setUp() throws Exception {
        c = Comparators.asc();
        Set<Date> dates = new TreeSet<Date>(c);
        while (dates.size() < size) {
            dates.add(new Date(r.nextLong()));
        }
        values = dates.toArray(new Date[]{});
    }

    @Override
    DictionaryEncodedColumn<Date> getDictColumn() {
        SwiftLoggers.getLogger().error(new Error());
        return new DateDictColumn(
                new ResourceLocation(BASE_PATH + "/dict/child_date"),
                c);
    }
}