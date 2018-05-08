package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class LongDetailColumnTest extends BasePrimitiveDetailColumnTest {
    @Override
    public void testPutThenGet() {
        long[] longs = r.longs(size, 0, size << 1).sorted().toArray();
        LongDetailColumn longDetailColumn = new LongDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_long"));
        for (int i = 0; i < longs.length; i++) {
            longDetailColumn.put(i, longs[i]);
        }
        longDetailColumn.release();

        longDetailColumn = new LongDetailColumn(
                new ResourceLocation(BASE_PATH + "/detail/child_long"));
        for (int i = 0; i < longs.length; i++) {
            assertEquals(longs[i], longDetailColumn.getLong(i));
        }
    }

}
