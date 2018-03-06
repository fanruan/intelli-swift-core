package com.fr.swift.segment.column.impl.base;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.location.ResourceLocation;
import junit.framework.TestCase;

import java.util.Random;

import static com.fr.swift.cube.io.BaseIoTest.CUBES_PATH;

/**
 * @author anchore
 * @date 2017/11/10
 */
public class BitMapColumnTest extends TestCase {
    static final String BASE_PATH = CUBES_PATH;
    Random r = new Random();
    int size = 1000000;

    public void testPutThenGet() {
        MutableBitMap m = BitMaps.newRoaringMutable();
        r.ints(size, 0, size << 1).forEach(m::add);

        BitMapColumn bc = new BitMapColumn(new ResourceLocation(BASE_PATH + "/index/child"));

        int pos = r.nextInt(10);
        bc.putBitMapIndex(pos, m);
        bc.putNullIndex(m);
        bc.release();

        bc = new BitMapColumn(new ResourceLocation(BASE_PATH + "/index/child"));
        ImmutableBitMap im = bc.getBitMapIndex(pos);
        ImmutableBitMap nullIm = bc.getNullIndex();
        m.traversal(row -> {
            if (!im.contains(row) || !nullIm.contains(row)) {
                fail();
            }
        });
    }
}
