package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.location.ResourceLocation;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class LongDetailColumnTest extends BaseDetailColumnTest {

    @Test
    public void testPutThenGet() {
        long[] longs = new long[size];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = r.nextLong();
        }
        Arrays.sort(longs);
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